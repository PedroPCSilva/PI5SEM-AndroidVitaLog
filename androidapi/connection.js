const mysql = require('mysql2');
const credentials = require('./credentials')

const con = mysql.createConnection({
        host: credentials.secrets.host,     // host for connection
        port: credentials.secrets.port,            // default port for mysql is 3306
        database: credentials.secrets.database,      // database from which we want to connect our node application
        user: credentials.secrets.user,          // username of the mysql connection
        password: credentials.secrets.password 
    })

con.connect(function(err) {
    if (err) {
        console.log("error occurred while connecting:\n"+err);
    } else {
        console.log("connection created with mysql successfully");
    }
});

module.exports = {
   con
}