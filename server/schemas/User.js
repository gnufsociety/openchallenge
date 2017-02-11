var mongoose = require('mongoose');

var UserSchema = new mongoose.Schema({
    username: String,
    picture:String,
    status: String,
    rate: Number,
    gold: Number,
    silver: Number,
    bronze: Number,
    uid: String
},{ versionKey: false });



module.exports = mongoose.model("User", UserSchema);