var express = require('express');
//var db = require('../db');
var assert = require('assert');
var mongoose = require('mongoose');
var User = require('../schemas/User');
var Challenge = require('../schemas/Challenge');
var ObjectId = require('mongoose').Types.ObjectId;


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
    User.findOne({'uid': obj.organizer}).exec(function (err, user) {
        assert.equal(err, null);
        var chall = new Challenge({
            name: obj.name,
            //picture: obj.picture,
            description: obj.description,
            rules: obj.rules,
            image: obj.image,
            location: {
                address: obj.location.address,
                lat: obj.location.lat,
                long: obj.location.long
            },
            date: obj.date,
            organizer: user._id,
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

});

router.get('/getNumParticipants/:id_chall/:user_uid', function (req, res, next) {
    User.findOne({'uid':req.params.user_uid}).exec(function (err, user) {
        Challenge.findOne({_id: req.params.id_chall})
            .exec(function (err, chall) {
                if (err) res.send("err")
                else {
                    var part = chall.participants;
                    var found = false;
                    for (var i = 0; i< part.length; i++){
                        if (part[i].toString() == user._id.toString()){
                            found = true;
                            break;
                        }
                    }
                    var obj = {'participants':part.length,
                                'you':found}
                    res.send(obj);
                }
            });
    });

});
router.get('/allChallenges', function (req, res, next) {

    Challenge.find()
        .populate('organizer')
        .populate({
            path: 'participants',
            select: 'username picture',
            options: {limit: 2}
        })
        .exec(function (error, chall) {
            res.send(chall);
        })
});


router.get('/addParticipant/:chall_id/:user_id', function (req, res) {
    var chall_id = req.params.chall_id;
    var user_id = req.params.user_id;
    User.findOne({'uid': user_id}).exec(function (err, user) {
        Challenge.findByIdAndUpdate(
            chall_id,
            {$addToSet: {"participants": user._id}},  // do not add if already present
            {safe: true, upsert: true},
            function (err) {
                if (err) {
                    res.send('err');
                    console.log(err);
                }
                else res.send('you participate now!')
            }
        );
    });


});

router.get('/removeParticipant/:chall_id/:user_id', function (req, res) {
    var chall_id = req.params.chall_id;
    var user_id = req.params.user_id;
    User.findOne({'uid': user_id}).exec(function (err, user) {
        Challenge.findByIdAndUpdate(
            chall_id,
            {$pull: {"participants": user._id}},
            {safe: true, upsert: true},
            function (err) {
                if (err) {
                    res.send('err');
                    console.log(err);
                }
                else res.send('removed!');
            }
        );
    });


});

router.get('/getParticipants/:chall_id', function (req, res, next) {
    Challenge.findById(req.params.chall_id)
        .populate('participants')
        .exec(function (err, chall) {
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
        picture: obj.picture,
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

router.get('/findUsers/:user', function (req, res) {
    var user = req.params.user;
    User.find({'username': new RegExp('^' + user)}).exec(function (err, users) {
        if (err) res.send('Error');
        else res.send(users);
    });
});

router.get('/findUserByUid/:uid', function (req, res) {
    var user_id = req.params.uid;
    User.findOne({'uid':user_id})
        .exec(function (err, user) {
            if (err) res.send("Error");
            else res.send(user);
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
