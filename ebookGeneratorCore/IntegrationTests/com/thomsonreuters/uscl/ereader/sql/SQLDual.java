package com.thomsonreuters.uscl.ereader.sql;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * This class exists as configuration for dbSetup to allow access to the DUAL table,
 * the dummy table used to evaluate expressions such as sysdate which do not require
 * actual table access
 */
@Entity
@Table(name = "DUAL")
public class SQLDual
{
    @Id
    @Column(name = "PID", nullable = false)
    private int id;
}
