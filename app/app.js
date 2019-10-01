var express = require('express'),
    path = require('path'),
    bodyParser = require('body-parser'),
    cons = require('consolidate'),
    app = express();
// DB connect string    
var connect = 'postgres://ems:EjJaIwV2uaAWGMUMKTqk@a204820-ccng-sampleapp-rds.c1td4aupmsdf.us-east-1.rds.amazonaws.com:5432/postgres';
const {Pool} = require("pg");
const pool = new Pool({
    connectionString: connect
});

//require() is used to load module

//app.set('view engine','dust');
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
            console.log(result)
            let re = result[1].fields.map((({ name }) => name));
            console.log("-----",re)
            res.render('employee.ejs',{employee: result[0].rows,columnNames: re});
            //res.render('employee.ejs',{employee: result.rows});
           // console.log('====',a)
            done();
        });

    });
    
});
app.post('/add',function(req,res){
    pool.connect(function(err,client,done){
    if(err){
        return console.error("error fetching client from pool"+ err);
        //res.status(400).send(err);
    }
    client.query('SELECT * FROM employee WHERE FALSE;SELECT * FROM employee',function(err,result){
        console.log('reqq',req.body,req.body);
        console.log('val>>>',Object.values(req.body));
        console.log('keys',Object.keys(req.body))
        //var a = Object.keys(result.rows[0]);
   
        var insertColumns='';
        var insertValues='';
        for(var i = 0; i < Object.values(req.body).length;i++){
            //insertColumns += ", " + a[i] ;
            insertValues += "$"+(i+1);
            if(i!=Object.values(req.body).length-1){
                insertValues+=',';
            }
          }
            
           var val=JSON.parse(JSON.stringify(req.body));
           let keys = Object.keys(req.body);
         client.query('INSERT INTO ' + 'employee'+ ' (' + keys.join(',') + ') VALUES(' + insertValues + ')',Object.values(val));
        });

    // client.query('SELECT * FROM employee',function(err, result){
    // var a = Object.keys(result.rows[0]);
   
    // var insertColumns='';
    // var insertValues='';
    // for(var i = 0; i < a.length;i++){
    //     insertColumns += ", " + a[i] ;
    //     insertValues += "$"+(i+1);
    //     if(i!=a.length-1){
    //         insertValues+=',';
    //     }
    //   }
        
    //    var val=JSON.parse(JSON.stringify(req.body));
    //    let keys = Object.keys(result.rows[0]);
    //  client.query('INSERT INTO ' + 'employee'+ ' (' + keys.join(',') + ') VALUES(' + insertValues + ')',Object.values(val));
    // });
     client.query('SELECT * FROM employee',function(err, result){
        //var a = Object.keys(result.rows[0]);
        //console.log('final>>>>',a);
    
    });
    done();
    res.redirect('/');
    });

});
//getting_column names
app.post('/addColumn',function(req,res){
    pool.connect(function(err,client,done){
    if(err){
        return console.error("error fetching client from pool"+ err);
        
    }
    col = '';
    col = 'ALTER TABLE employee ADD '+req.body.cname +' '+ 'varchar(255)';
    client.query(col);
    client.query('SELECT * FROM employee',function(err, result){
    });
    done();
    res.redirect('/');
});
});
app.post('/deleteColumn',function(req,res){
    pool.connect(function(err,client,done){
    if(err){
        return console.error("error fetching client from pool"+ err);
        
    }
    col = '';
    col = 'ALTER TABLE employee DROP '+req.body.delColname ;
    console.log('col>>>>',col);
    client.query(col);
    client.query('SELECT * FROM employee',function(err, result){
    });
    done();
    res.redirect('/');
});
});

app.delete('/delete/:id',function(req,res){
    console.log('++++')
    pool.connect(function(err,client,done){
        if(err){
            return console.error("error fetching client from pool"+ err);
            //res.status(400).send(err);
        }
        client.query("DELETE FROM employee where id = $1", [req.params.id]);
        
        done();
        res.sendStatus(200);
        });
});

app.get('/employee/edit/:id',function (req, res) {

    var id = req.params.id;
    console.log('id',id);
    pool.connect(function(err,client,done){
    client.query('SELECT * FROM employee WHERE id=$1', [id], function (err, result) {
        if (err) {
            console.log(err);
            res.status(400).send(err);
        }
        console.log("id>>>>",result.rows[0].id)
        console.log(Object.keys(result.rows[0]));
        res.render('employee/edit', { title: "Edit Employee", data: result.rows });
    });
    });
});

app.post('/employee/edit/:id', function (req, res) {
    pool.connect(function(err,client,done){
        if(err){
            return console.error("error fetching client from pool"+ err);
            //res.status(400).send(err);
        }
    client.query("SELECT column_name FROM INFORMATION_SCHEMA.COLUMNS where table_name= N'employee' ",function(err, result){
        var a = (result.rows);
       console.log('a----',a)
        var insertColumns='';
        ar=[];
        var insertValues='';
        var val=JSON.parse(JSON.stringify(req.body));
        //let keys = Object.keys(result.rows[0]);
        //console.log('keys',keys)
        value=Object.values(val);
        for(var i = 0; i < a.length;i++){
            insertColumns +=  a[i]['column_name'] ;
            insertValues += "$"+(i+1);
            ar.push(a[i]['column_name']+'='+value[i])
            if(i!=a.length-1){
                insertValues+=',';
                insertColumns+=',';
            }
          }
       
                // Setup static beginning of query
                var update = 'UPDATE employee SET ';
                //query.push('SET');
              
                // Create another array storing each set command
                // and assigning a number value for parameterized query
                var set = [];
                Object.keys(val).forEach(function (key, i) {
                  set.push(key + " = '"  + value[i] +"'" ); 
                });
                update += set.join(', ');
                update += ' WHERE id = ' + "'"+req.body.id+"'" ;
                client.query(update, function (err, result) {
                    if (err) {
                        console.log("Error Updating : %s ", err);
                    }
                    console.log('update')
                    res.redirect('/');
                });
        
        });
    });
});




//server
app.listen(8080,function(){
    console.log('Server started on postgres');
});