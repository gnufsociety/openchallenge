var mongoose = require('mongoose');

var ChallengeSchema = new mongoose.Schema({
    name: String,
    description: String,
    rules: String,
    image: String,
    location: {
        address: String,
        lat: Number,
        long: Number
    },
    date: String,
    organizer: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User'
    },
    participants: [{
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User'
    }]
},{ versionKey: false });

module.exports = mongoose.model("Challenge", ChallengeSchema);