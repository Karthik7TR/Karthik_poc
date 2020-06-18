const cucumber = require('cucumber');
const chai = require('chai');
const request = require('sync-request');

let PROTOCOL = process.env.APPLICATION_PROTOCOL || "http://";
let APPLICATION_DNS = process.env.APPLICATION_DNS || "localhost:8080";
let HEADERS = { "X-BlueGreen-Routing": "green" };

// World
class CustomWorld {
    x;
    y;
    response;  
} 
cucumber.setWorldConstructor(CustomWorld);

// Step definitions.
cucumber.Given('two variables {} and {}', function(x, y) {
    this.x = x;
    this.y = y;
});
cucumber.When('I call the {} endpoint', function(endpoint) {
    let url = PROTOCOL + APPLICATION_DNS + endpoint;
    this.response = request('GET', url,
        {
            qs: {"x": this.x, "y": this.y},
            headers: HEADERS,
        }
        
    );
});
cucumber.Then('the result should be {}', function(result) {
    chai.expect(this.response.body.toString()).to.eql(result);
});
cucumber.Then('the result should contain {}', function(result) {
    chai.expect(this.response.body.toString()).to.include(result);
});
cucumber.Then('the status code should be {int}', function(statusCode) {
    chai.expect(this.response.statusCode).to.eql(statusCode);
});
