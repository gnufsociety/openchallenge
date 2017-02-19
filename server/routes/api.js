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

router.post('/setWinners', function (req, res) {
    var obj = req.body;
    var errors = false;
    User.findByIdAndUpdate(
        obj.first,
        {$inc: {'gold': 1}},
        {safe:true, upsert:true},
        function (err) {
            errors = errors || err;
            User.findByIdAndUpdate(
                obj.second,
                {$inc: {'silver': 1}},
                {safe:true, upsert:true},
                function (err) {
                    errors = errors || err;
                    User.findByIdAndUpdate(
                        obj.third,
                        {$inc: {'bronze': 1}},
                        {safe:true, upsert:true},
                        function (err) {
                            errors = errors || err;
                            console.log(errors);
                            res.send(errors);
                        });
                });
        });
});

router.post('/newChallenge', function (req, res) {
    var obj = req.body;
    User.findOne({'uid': obj.organizer}).exec(function (err, user) {
        assert.equal(err, null);
        var challenge = new Challenge({
            name: obj.name,
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
        challenge.save(function (err) {
            if (err) {
                res.send("Error");
                console.log("Error saving new challenge");
            } else {
                res.send("Saved!");
            }
        })
    });

});

router.get('/getNumParticipants/:id_chall/:user_uid', function (req, res, next) {
    User.findOne({'uid': req.params.user_uid})
        .exec(function (err, user) {
            Challenge.findOne({_id: req.params.id_chall})
                .exec(function (err, chall) {
                    if (err) res.send("err");
                    else {
                        var part = chall.participants;
                        var found = false;
                        for (var i = 0; i < part.length; i++) {
                            if (part[i].toString() == user._id.toString()) {
                                found = true;
                                break;
                            }
                        }
                        var obj = {
                            'participants': part.length,
                            'you': found
                        };
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
            options: {limit: 3}
        })
        .exec(function (error, challenge) {
            assert.equal(error, null);
            res.send(challenge);
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
            assert.equal(err, null);
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
    User.findOne({'username':user.username}).exec(function (err, u) {
        if (err) res.send("Error!");
        else if (u){
            res.send("already exist");
        }
        else {
            user.save(function (err) {
                if (err) res.send("Errore");
                else res.send("User " + user.username + " created!");
            });
        }
    });

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
    User.findOne({'uid': user_id})
        .exec(function (err, user) {
            if (err) res.send("Error");
            if(user)
                res.send(user);
            else
                res.send("No user found");
        });
});


module.exports = router;
