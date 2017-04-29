var express = require('express');
//var db = require('../db');
var assert = require('assert');
var mongoose = require('mongoose');
var User = require('../schemas/User');
var Challenge = require('../schemas/Challenge');
var Version = require('../schemas/Version');
var ObjectId = require('mongoose').Types.ObjectId;


var router = express.Router();   // get router instance

// Connection URL
var db_url = 'mongodb://localhost:27017/openchallenge';

mongoose.connect(db_url);

router.get('/', function (req, res, next) {
    res.send("Ciao chicco!");
});


router.get('/checkUpdate/:my_version', function (req, res) {
    var v = req.params.my_version;
    Version.findOne().exec(function (err, version) {
        if (err) res.send("Errore");
        else {
            if (v != version.version)
                res.send("yes");
            else res.send("no");
        }
    })
});

router.get('/newUpdate/:my_version', function (req, res) {
    var v = new Version({'version': req.params.my_version});
    Version.findOneAndUpdate({}, {'version': req.params.my_version}, { upsert: true} ).exec(function (err) {
        if (err) res.send("Errrore");
        else res.send("Updated!")
    });
});

/*****************************************************************************
 *
 * API to manage CHALLENGES
 *
 */


router.post('/newChallenge', function (req, res) {
    var obj = req.body;
    User.findOne({'uid': obj.organizer})
        .exec(function (err, user) {
            assert.equal(err, null);
            var dat = obj.date.split("/");
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
                organizer: user._id,
                participants: [],
                invited: [],
                isTerminated: false
            });
            /*
            challenge.date = new Date(Date.UTC(dat[2], dat[1] - 1, dat[0], 0, 0, 0, 0));
            if (challenge.date < Date.now()) challenge.isTerminated = true;
            */
            challenge.save(function (err, chall) {
                if (err) {
                    res.send("Error");
                    console.log("Error saving new challenge");
                } else {
                    User.findByIdAndUpdate(
                        user._id,
                        {$addToSet: {organizedChallenges: chall._id}},
                        function (errr) {
                            if (errr) {
                                res.send("Error updating users");
                            }
                            else res.send("Saved!");
                        }
                    )

                }
            })
        });
});


router.get('/getNumParticipants/:id_chall/:user_uid', function (req, res, next) {
    var user_uid = req.params.user_uid;
    var chall_id = req.params.id_chall;
    User.findOne({'uid': user_uid})
        .exec(function (err, user) {
            Challenge.aggregate([
                {$match: {'_id': mongoose.Types.ObjectId(chall_id)}},
                { //add number participants field
                    $addFields: {
                        numParticipants: {$size: '$participants'}
                    }
                },
                { //select only participants array that will be populated later and numPart
                    $project: {
                        numParticipants: 1,
                        _id: 0,
                        participants: 1
                    }
                }

            ]).exec(function (err, chall) {
                if (err) res.send("Err");

                else {
                    //check if you participate in this challenge (we can change with joined challenges)
                    var part = chall[0].participants;
                    var found = false;
                    for (var i = 0; i < part.length; i++) {
                        if (part[i].toString() == user._id.toString()) {
                            found = true;
                            break;
                        }
                    }

                    Challenge.populate(chall,
                        {
                            path: 'participants',
                            select: 'picture',
                            options: {limit: 3}
                        },
                        function (err, challPop) {
                            //adding youparticipate field
                            challPop[0].joined = found;
                            res.send(challPop)
                        })
                }
            });
        });
});


router.get('/allChallenges', function (req, res, next) {

    Challenge.find()
        .select('name description rules image location date organizer isTerminated')
        .sort({date: 1})
        .populate({
            path: 'organizer'
        })
        .exec(function (err, challenge) {
            assert.equal(err, null);
            res.send(challenge);
        })
});


router.get('/activeChallenges', function (req, res) {
    Challenge.find({isTerminated: false})
		.select('name description rules image location date organizer isTerminated')
        .sort({date: 1})
        .populate({
            path: 'organizer'
        })
        .exec(function (err, challenge) {
            assert.equal(err, null);
            res.send(challenge);
        })
});


router.get('/addParticipant/:challenge_id/:user_id', function (req, res) {
    var challenge_id = req.params.challenge_id;
    var user_id = req.params.user_id;
    User.findOneAndUpdate({'uid': user_id}, {$addToSet: {joinedChallenges: challenge_id}}) // add to joined list
        .exec(function (err, user) {
            if (err) res.send("Error in finding and updating joined list");
            else {
                Challenge.findByIdAndUpdate(
                    challenge_id,
                    {$addToSet: {"participants": user._id}},  // do not add if already present
                    {safe: true, upsert: true},
                    function (err) {
                        if (err) {
                            res.send('Error in finding and updating participants list');
                            console.log(err);
                        }
                        else res.send('you participate now!')
                    }
                );
            }
        });
});


router.get('/removeParticipant/:chall_id/:user_id', function (req, res) {
    var chall_id = req.params.chall_id;
    var user_id = req.params.user_id;
    User.findOneAndUpdate({'uid': user_id}, {$pull: {joinedChallenges: chall_id}})  // remove from joined list
        .exec(function (err, user) {
            if (err) res.send("Error");
            else {
                Challenge.findByIdAndUpdate(
                    chall_id,
                    {$pull: {"participants": user._id}},
                    {safe: true, upsert: true},
                    function (err) {
                        if (err) {
                            res.send('Error');
                            console.log(err);
                        }
                        else res.send('removed!');
                    }
                );
            }
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


router.get('/terminateChallenge/:challenge_id', function (req, res) {
    Challenge.findByIdAndUpdate(req.params.challenge_id,
        {isTerminated: true},
        function (err, challenge) {
            assert.equal(err, null);
            res.send("Challenge " + challenge.name + " terminated")
        });
});


router.post('/setWinners/:ch_id', function (req, res) {
    var podium = req.body;
    Challenge.findByIdAndUpdate(req.params.ch_id,
        {
            "winner": ObjectId(podium.first),
            "secondPlace": ObjectId(podium.second),
            "thirdPlace": ObjectId(podium.third)
        },
        function (err, challenge) {
            if (err) console.log(err);
            assert.equal(err, null);
        });
    // update medals
    var errors = false;
    User.findByIdAndUpdate(
        podium.first,
        {$inc: {'gold': 1}},
        {safe: true, upsert: true},
        function (err) {
            errors = errors || err;
            User.findByIdAndUpdate(
                podium.second,
                {$inc: {'silver': 1}},
                {safe: true, upsert: true},
                function (err) {
                    errors = errors || err;
                    User.findByIdAndUpdate(
                        podium.third,
                        {$inc: {'bronze': 1}},
                        {safe: true, upsert: true},
                        function (err) {
                            errors = errors || err;
                            assert.equal(errors, null);
                            console.log(errors);
                            res.send(errors);
                        });
                });
        });
});


router.get('/winners/:challenge_id', function (req, res) {
    Challenge.findById(req.params.challenge_id)
        .populate('winner secondPlace thirdPlace')
        .exec(function (err, challenge) {
            assert.equal(err, null);
            var winners = [];
            winners.push(challenge.winner);
            winners.push(challenge.secondPlace);
            winners.push(challenge.thirdPlace);
            res.json(winners);
        });

});

router.get('/invite/:challenge_id/:user_id', function (req, res) {
    var challenge_id = req.params.challenge_id;
    var user_id = req.params.user_id;
    User.findByIdAndUpdate(user_id, {$addToSet: {invitations: challenge_id}})
        .exec(function (err, user) {
            if (err) res.send("Error in finding and updating invitations list");
            else {
                Challenge.findByIdAndUpdate(
                    challenge_id,
                    {$addToSet: {"invitedList": user._id}},  // do not add if already present
                    {safe: true, upsert: true},
                    function (err) {
                        if (err) {
                            res.send('Error in finding and updating invited list');
                            console.log(err);
                        } else res.send('you have been invited!')
                    });
            }
        });
});

router.get('/cancelInvite/:challenge_id/:user_id', function (req, res) {
    var challenge_id = req.params.challenge_id;
    var user_id = req.params.user_id;
    User.findByIdAndUpdate(user_id, {$pull: {invitations: challenge_id}})
        .exec(function (err, user) {
            if (err) res.send("Error in finding and updating invitations list");
            else {
                Challenge.findByIdAndUpdate(
                    challenge_id,
                    {$pull: {"invitedList": user._id}},  // do not add if already present
                    {safe: true, upsert: true},
                    function (err) {
                        if (err) {
                            res.send('Error in finding and updating invited list');
                            console.log(err);
                        } else res.send('you participate now!')
                    });
            }
        });
});

router.get('/usersInvited/:challenge_id', function (req, res) {
    Challenge.findById(req.params.challenge_id)
        .populate('invitedList')
        .exec(function (err, challenge) {
            assert.equal(err, null);
            res.send(challenge.invitedList);
        });
});

/*****************************************************************************
 *
 * API to manage USERS
 *
 */

router.post('/newUser', function (req, res) {
    var obj = req.body;
    var user = new User({
        username: obj.username,
        status: obj.status,
        picture: obj.picture,
        rate: 0,
        gold: 0,
        silver: 0,
        bronze: 0,
        uid: obj.uid,
        joinedChallenges: [],
        organizedChallenges: [],
        invitations: [],
        following: []
    });
    User.findOne({'username': user.username}).exec(function (err, u) {
        if (err) res.send("Error!");
        else if (u) {
            res.send("already exists");
        }
        else {
            user.save(function (err) {
                if (err) res.send("Error");
                else res.send("User " + user.username + " created!");
            });
        }
    });

});

router.get('/deleteUser/:user', function (req, res) {
    User.deleteOne({_id: req.params.user})
        .exec(function (err) {
            assert.equal(err, null);
            res.send('deleted');
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
    User.find({'username': new RegExp('^' + user, "i")}).exec(function (err, users) {
        if (err) res.send('Error');
        else res.send(users);
    });
});

router.get('/findUserByUid/:uid', function (req, res) {
    var user_id = req.params.uid;
    User.findOne({'uid': user_id})
        .exec(function (err, user) {
            if (err) res.send("Error");
            if (user)
                res.send(user);
            else
                res.send("No user found");
        });
});

router.get('/organizedChallenges/:user_id', function (req, res) {
    User.findById(req.params.user_id)
        .populate({
            path: 'organizedChallenges',
            populate: {
                path: 'organizer',
                select: 'username picture uid rate'
            }
        })
        .exec(function (err, user) {
            assert.equal(err, null);
            res.json(user.organizedChallenges);
        });
});

router.get('/joinedChallenges/:user_id', function (req, res) {
    User.findById(req.params.user_id)
        .populate({
            path: 'joinedChallenges',
            populate: {
                path: 'organizer',
                select: 'username picture uid rate'
            }
        })
        .exec(function (err, user) {
            assert.equal(err, null);
            res.json(user.joinedChallenges);
        });
});

/**
 * request must be a json object in this form:
 * {
 *    "new_status" : "text of the new status"
 * }
 */
router.post('/setStatus/:user_id', function (req, res) {
    User.findByIdAndUpdate(req.params.user_id, {status: req.body.new_status},
        function (err, user) {
            assert.equal(err, null);
            res.send("Successfully updated status of " + user.username);
        });
});

router.get('/follow/:user_uid/:followed', function (req, res) {
    User.findOneAndUpdate({'uid': req.params.user_uid},
        {$addToSet: {'following': req.params.followed}},
        function (err) {
            if (err) console.log(err);
            assert.equal(err, null);
            res.send(err);
        });
});

router.get('/unfollow/:user_uid/:followed', function (req, res) {
    User.findOneAndUpdate({'uid': req.params.user_uid},
        {$pull: {'following': req.params.followed}},
        function (err) {
            if (err) console.log(err);
            assert.equal(err, null);
            res.send(err);
        });
});

router.get('/following/:user_uid', function (req, res) {
    User.findOne({'uid': req.params.user_uid})
        .populate({
            path: 'following'
        })
        .exec(function (err, user) {
            assert.equal(err, null);
            res.json(user.following);
        });
});

router.post('/setWinners', function (req, res) {
    var obj = req.body;
    var errors = false;
    User.findByIdAndUpdate(
        obj.first,
        {$inc: {'gold': 1}},
        {safe: true, upsert: true},
        function (err) {
            errors = errors || err;
            User.findByIdAndUpdate(
                obj.second,
                {$inc: {'silver': 1}},
                {safe: true, upsert: true},
                function (err) {
                    errors = errors || err;
                    User.findByIdAndUpdate(
                        obj.third,
                        {$inc: {'bronze': 1}},
                        {safe: true, upsert: true},
                        function (err) {
                            errors = errors || err;
                            console.log(errors);
                            res.send(errors);
                        });
                });
        });
});

router.get('/pendingInvitations/:user_id', function (req, res) {
    User.findById(req.params.user_id)
        .populate({
            path: 'invitations',
            populate: {
                path: 'organizer',
                select: 'username picture uid rate'
            }
        })
        .exec(function (err, user) {
            assert.equal(err, null);
            res.json(user.invitations);
        });
});

module.exports = router;
