var mongoose = require('mongoose');

var UserSchema = new mongoose.Schema({
        username: {type : String, trim : true },
        picture:  String,
        status:   {type : String, trim : true },
        rate:     Number,
        gold:     Number,
        silver:   Number,
        bronze:   Number,
        uid:      {type: String,
                   required: true}
    },{ versionKey: false });
    // no version key can get us into troubles: why is it set to false?


module.exports = mongoose.model("User", UserSchema);