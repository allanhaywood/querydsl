/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.query.sql;

import java.io.File;
import java.sql.SQLException;
import java.util.Set;

import javax.tools.JavaCompiler;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.mysema.codegen.SimpleCompiler;
import com.mysema.query.AbstractJDBCTest;

public class MetaDataSerializerTest extends AbstractJDBCTest{

    @Override
    @Before
    public void setUp() throws SQLException, ClassNotFoundException{
        super.setUp();
        statement.execute("drop table employee if exists");
        statement.execute("drop table survey if exists");
        statement.execute("drop table date_test if exists");
        statement.execute("drop table date_time_test if exists");

        // survey
        statement.execute("create table survey (id int, name varchar(30), "
                + "CONSTRAINT PK_survey PRIMARY KEY (id, name))");
        
        // date_test
        statement.execute("create table date_test (d date)");
        
        // date_time
        statement.execute("create table date_time_test (dt datetime)");
        
        // employee
        statement.execute("create table employee("
                + "id INT, "
                + "firstname VARCHAR(50), "
                + "lastname VARCHAR(50), "
                + "salary DECIMAL(10, 2), "
                + "datefield DATE, "
                + "timefield TIME, "
                + "superior_id int, "
                + "survey_id int, "
                + "survey_name varchar(30), "
                + "CONSTRAINT PK_employee PRIMARY KEY (id), "
                + "CONSTRAINT FK_survey FOREIGN KEY (survey_id, survey_name) REFERENCES survey(id,name), "
                + "CONSTRAINT FK_superior FOREIGN KEY (superior_id) REFERENCES employee(id))");
    }

    @Test
    public void Normal_serialization() throws SQLException{
        String namePrefix = "Q";
        NamingStrategy namingStrategy = new DefaultNamingStrategy();
        // customization of serialization
        MetaDataExporter exporter = new MetaDataExporter();
        exporter.setNamePrefix(namePrefix);
        exporter.setPackageName("test");
        exporter.setTargetFolder(new File("target/cust1"));
        exporter.setNamingStrategy(namingStrategy);
        exporter.export(connection.getMetaData());

        compile(exporter);
    }

    private void compile(MetaDataExporter exporter) {
        JavaCompiler compiler = new SimpleCompiler();
        Set<String> classes = exporter.getClasses();
        int compilationResult = compiler.run(null, null, null, classes.toArray(new String[classes.size()]));
        if(compilationResult == 0){
            System.out.println("Compilation is successful");
        }else{
            Assert.fail("Compilation Failed");
        }
    }

}
