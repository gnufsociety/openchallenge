var mongoose = require('mongoose');

var ChallengeSchema = new mongoose.Schema({
        name:        {type : String, trim : true },
        description: {type : String, trim : true },
        rules:       {type : String, trim : true },
        image: String,
        location: {
            address: String,
            lat:     Number,
            long:    Number
        },
        date: Date,
        organizer: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'User',
            required: true
        },
        participants: [{
            type: mongoose.Schema.Types.ObjectId,
            ref: 'User'
        }],
        isTerminated: {type: Boolean, default: false},
        winner: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'User'
        },
        secondPlace: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'User'
        },
        thirdPlace: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'User'
        }
    },{ versionKey: false });
    // no version key can get us into troubles: why is it set to false?


module.exports = mongoose.model("Challenge", ChallengeSchema);