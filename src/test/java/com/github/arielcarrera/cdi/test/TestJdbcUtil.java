package com.github.arielcarrera.cdi.test;

import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.h2.jdbcx.JdbcDataSource;

import com.github.arielcarrera.cdi.test.config.TransactionalConnectionProvider;
import com.github.arielcarrera.cdi.test.entities.CacheableEntity;
import com.github.arielcarrera.cdi.test.entities.TestEntity;

public class TestJdbcUtil {

    public static TestEntity jdbcGetById(Integer id) {
	try {
	    JdbcDataSource dataSource = TransactionalConnectionProvider.getDataSource();
	    try (Connection connection = dataSource.getConnection()) {
		try (PreparedStatement statement = connection.prepareStatement(
			"SELECT `id`,`value`,`uniqueValue`,`status` FROM `TestEntity` WHERE `id` = ?")) {
		    statement.setInt(1, id);
		    try (ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
			    return new TestEntity(resultSet.getInt("id"), resultSet.getInt("value"),
				    resultSet.getInt("uniqueValue"), resultSet.getInt("status"));
			}
		    }
		}
	    }
	} catch (Exception e) {
	    fail("Invalid Test JDBC Configuration");
	}
	return null;
    }

    public static boolean jdbcExistById(Integer id) {
	try {
	    JdbcDataSource dataSource = TransactionalConnectionProvider.getDataSource();
	    try (Connection connection = dataSource.getConnection()) {
		try (PreparedStatement statement = connection
			.prepareStatement("SELECT `id` FROM `TestEntity` WHERE `id` = ?")) {
		    statement.setInt(1, id);
		    try (ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
			    return true;
			}
		    }
		}
	    }
	} catch (Exception e) {
	    fail("Invalid Test JDBC Configuration");
	}
	return false;
    }

    public static List<TestEntity> jdbcGetAll() {
	List<TestEntity> list = new ArrayList<>();
	try {
	    JdbcDataSource dataSource = TransactionalConnectionProvider.getDataSource();
	    try (Connection connection = dataSource.getConnection()) {
		try (PreparedStatement statement = connection
			.prepareStatement("SELECT `id`,`value`,`uniqueValue`,`status` FROM `TestEntity`")) {

		    try (ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
			    list.add(new TestEntity(resultSet.getInt("id"), resultSet.getInt("value"),
				    resultSet.getInt("uniqueValue"), resultSet.getInt("status")));
			}
		    }
		}
	    }
	} catch (Exception e) {
	    fail("Invalid Test JDBC Configuration");
	}
	return list;
    }

    public static long jdbcCountAll() {
	try {
	    JdbcDataSource dataSource = TransactionalConnectionProvider.getDataSource();
	    try (Connection connection = dataSource.getConnection()) {
		try (PreparedStatement statement = connection
			.prepareStatement("SELECT count(`id`) FROM `TestEntity`")) {
		    try (ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
			    return resultSet.getLong(1);
			}
		    }
		}
	    }
	} catch (Exception e) {
	    fail("Invalid Test JDBC Configuration");
	}
	return -1;
    }

    public static TestEntity jdbcPutCacheable(CacheableEntity entity) {
	try {
	    JdbcDataSource dataSource = TransactionalConnectionProvider.getDataSource();
	    try (Connection connection = dataSource.getConnection()) {
		try (PreparedStatement statement = connection.prepareStatement(
			"INSERT INTO `CacheableEntity` (`id`,`value`,`uniqueValue`,`status`) VALUES (?,?,?,?)")) {
		    statement.setInt(1, entity.getId());
		    statement.setInt(2, entity.getValue());
		    statement.setInt(3, entity.getUniqueValue());
		    statement.setInt(4, entity.getStatus());
		    statement.executeUpdate();
		}
	    }
	} catch (Exception e) {
	    fail("Invalid Test JDBC Configuration");
	}
	return null;
    }

}
