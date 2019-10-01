var express = require('express'),
    path = require('path'),
    bodyParser = require('body-parser'),
    app = express();
// DB connect string    
var connect = 'postgres://ems:EjJaIwV2uaAWGMUMKTqk@a204820-ccng-sampleapp-rds.c1td4aupmsdf.us-east-1.rds.amazonaws.com:5432/postgres';
const {Pool} = require("pg");
const pool = new Pool({
    connectionString: connect
});
//require() is used to load module

app.set('view engine','ejs')
app.set('views',__dirname + '/views');

//set public folder
app.use(express.static(path.join(__dirname, 'public')));

//Body parser middleware
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));

app.get('/',function(req,res){
    //pg connect
    pool.connect(function(err,client,done){
        if(err){
            return console.error("error fetching client from pool"+ err);
            //res.status(400).send(err);
        }
        client.query('SELECT * FROM employee;SELECT * FROM EMPLOYEE WHERE FALSE;',function(err, result){
            if(err){
                return console.error('error running query',err);
            }
            let re = result[1].fields.map((({ name }) => name));
            res.render('employee.ejs',{employee: result[0].rows,columnNames: re});
            done();
        });
    });
});

//server
app.listen(8080,function(){
    console.log('Server started on postgres');
});