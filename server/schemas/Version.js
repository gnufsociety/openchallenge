var mongoose = require('mongoose');

var VersionSchema = new mongoose.Schema({
    'version': String
});

module.exports = mongoose.model("Version", VersionSchema);

