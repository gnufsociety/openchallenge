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
            assert.equal(null, err);
            res.send(docs[0]);


        });

    });
});

router.get('/allChallenges', function (req, res) {
    db.get().collection('challenges').find().toArray(function (err, docs) {
       assert.equal(err, null);
       // res.json(docs);
       res.send('[{"_id":"589da3f3e5ac0950b0405b0a","organizer":"3niTtjEL09W4ACIpv9i30Ai4d9A2","name":"lancio di caccole","description":"chi le tira piu lontano","rules":"senza dita dei piedi","image":"lancio_di_caccole208","date":"10/1/2017","location":{"address":"Via Ariosto, 00185 Roma, Italy","lat":41.89054219999999,"long":12.504060699999998}}]');


    });

});

// USERS

router.post('/newUser', function (req, res) {
    var body_obj = req.body;
    db.get().collection('users').insertOne(body_obj, function (err, result) {
        assert.equal(err, null);

        res.send("Created user:" + body_obj.nickname);


    })

});

router.get('allUsers', function (req, res) {
    db.get().collection('users').find().limit(1).toArray(function (err, docs) {
        assert.equal(err, null);
        res.json(docs);


    });
});
    }
});


module.exports = router;
