var express = require('express'),
    path = require('path'),
    bodyParser = require('body-parser'),
    app = express();
//require() is used to load module

app.set('view engine','ejs')
app.set('views',__dirname + '/views');

//set public folder
app.use(express.static(path.join(__dirname, 'public')));

//Body parser middleware
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));

// Main page
app.get('/', function(req,res) {
    res.render('employee.ejs');
});

// Math API
app.get(['/add', '/subtract', '/multiply'], function(req,res) {
    let x = parseInt(req.query.x);
    let y = parseInt(req.query.y);

    if (isNaN(x) || isNaN(y)) {
        res.status(400).send("400: Bad request. Expected two integer query parameters 'x' and 'y'");
        return;
    }
    
    let operations = {
        "/add":      (x, y) => x + y,
        "/subtract": (x, y) => x - y,
        "/multiply": (x, y) => x * y,
    }
    let result = operations[req.path](x, y).toString();
    res.status(200).send(result);
});

// Server
app.listen(8080,function(){
    console.log('Server started.');
});
