var express = require('express');
var db = require('../db');
var assert = require('assert');

var router = express.Router();

// Connection URL
var db_url = 'mongodb://localhost:27017/openchallenge';

db.connect(db_url, function (err, done) {
    if (err) {
        console.log('API: Unable to connect to Mongo.');
        process.exit(1);
    } else {

    }
});

/* GET home page. */
router.get('/', function(req, res, next) {
  res.send("Hello World!");
});

// CHALLENGES
router.post('/newChallenge', function (req, res) {
    var body_obj = req.body;
    db.get().collection('challenges').insertOne(body_obj, function (err, result) {
        assert.equal(err, null);

        db.get().collection('challenges').find().limit(1).toArray(function (err, docs) {
            assert.equal(err, null);
            res.send(docs[0]);

            db.close();
        });
        
    });
});

// USERS




module.exports = router;
