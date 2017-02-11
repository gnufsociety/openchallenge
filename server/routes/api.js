var express = require('express');
//var db = require('../db');
var assert = require('assert');
var mongoose = require('mongoose');
var User = require('../schemas/User');
var Challenge = require('../schemas/Challenge');


var router = express.Router();   // get router instance

// Connection URL
var db_url = 'mongodb://localhost:27017/openchallenge';

mongoose.connect(db_url);

router.get('/', function (req, res, next) {
    res.send("Ciao chicco!");
});

/**
* Challenge api
* */

router.post('/newChallenge', function (req, res) {
    var obj = req.body;
    var chall = new Challenge({
        name: obj.name,
        picture: obj.picture,
        description: obj.description,
        rules: obj.rules,
        image: obj.image,
        location: {
            address: obj.location.address,
            lat: obj.location.lat,
            long: obj.location.long
        },
        date: obj.date,
        organizer: obj.organizer,
        participants: []
    });
    chall.save(function (err) {
        if (err) {
            res.send("Error");
        }
        else {
            res.send("Saved!");
        }
    })
});

router.get('/allChallenges', function (req, res, next) {

    Challenge.find()
        .populate('organizer')
        .exec(function (error, chall) {
            console.log(JSON.stringify(chall, null, "\t"));
            res.send(chall);
        })
});


router.get('/addParticipant/:chall_id/:user_id', function (req, res) {
    var chall_id = req.params.chall_id;
    var user_id = req.params.user_id;

    Challenge.findByIdAndUpdate(
        chall_id,
        {$addToSet: {"participants": user_id}},  // do not add if already present
        {safe: true, upsert: true},
        function (err) {
            if (err) {
                res.send('err');
                console.log(err);
            }
            else res.send('you participate now!')
        }
    )

});

router.get('/removeParticipant/:chall_id/:user_id', function (req, res) {
    var chall_id = req.params.chall_id;
    var user_id = req.params.user_id;

    Challenge.findByIdAndUpdate(
        chall_id,
        {$pull: {"participants": user_id}},
        {safe: true, upsert: true},
        function (err) {
            if (err) {
                res.send('err');
                console.log(err);
            }
            else res.send('removed!');
        }
    )

});

router.get('/getParticipants/:chall_id', function (req, res, next) {
    Challenge.findById(req.params.chall_id)
        .populate('participants')
        .exec( function (err, chall) {
            res.send(chall.participants);
        })
});


/**
 * User api
 */


router.post('/newUser', function (req, res, next) {
    var obj = req.body;
    var user = new User({
        username: obj.username,
        status: obj.status,
        rate: 0,
        gold: 0,
        silver: 0,
        bronze: 0,
        uid: obj.uid
    });
    user.save(function (err) {
        if (err) res.send("Errore");
        else res.send("User " + user.username + " created!");
    })
});

router.get('/allUsers', function (req, res) {
    User.find(function (err, users) {
        assert.equal(err, null);
        res.send(users);
    });
});


/*db.connect(db_url, function (err, done) {
 if (err) {
 console.log('[API] db.connect(db_url, ...): Unable to connect to MongoDB.');
 process.exit(1);
 } else {

 /** GET home page.
 *
 *
 router.get('/', function (req, res, next) {
 res.send("Hello World!");
 });

 /**
 * CHALLENGES APIs
 *

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
 var count = 0;
 docs.forEach(function (challenge, i, docs) {
 db.get().collection('users').findOne({'uid': challenge.organizer},
 function (er, item) {
 count++;
 assert.equal(er, null)
 challenge.organizer = item;
 if (count == docs.length){
 res.json(docs);
 res.send();
 }
 })
 })

 });

 });


 /**
 * USERS APIs
 *

 router.post('/newUser', function (req, res) {
 var body_obj = req.body;
 db.get().collection('users').insertOne(body_obj, function (err, result) {
 assert.equal(err, null);
 res.send("Created user:" + body_obj.nickname);
 })

 });


 router.get('/allUsers', function (req, res) {
 db.get().collection('users').find().toArray(function (err, docs) {
 assert.equal(err, null);
 res.json(docs);

 });
 });

 }
 });*/


module.exports = router;
