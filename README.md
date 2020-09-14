# MRO
A command-line tool to generate Java code from SQL files

### Example
```SQL
-- @name findUserByEmail
-- @type query
-- @result one
-- @return User
SELECT id, name, email
FROM users where email = :email;
```

Gives you 
```Java
public User findUserByEmail(java.lang.Object email, RowMapper<User> rowMapper) {
  Object[] params = { email };
  try {
    return this.jdbcTemplate.queryForObject("SELECT id, name, email\n"
            + "FROM users where email = ?;\n", params, rowMapper);
  } catch (java.lang.Exception e) {
    return null;
  }
}
```
(the generated Java code has [JdbcTemplate](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/JdbcTemplate.html) as a dependency.

### Try it
you can download the Jar file [here](https://github.com/alidn/MRO/releases/tag/0.1) in the release page, and run it on the command line. Example:
```
java -jar generator.jar /absolute/path/queries.sql /absolute/path/src/main/java/codegenerator
```
will create a `Queries.java` file in `/absolute/path/src/main/java/codegenerator` from `/absolute/path/queries.sql`.
Note that the paths must be absolute
