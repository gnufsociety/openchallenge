/**
 * Created by davidespallaccini on 10/02/17.
 */

var MongoClient = require("mongodb").MongoClient;
var assert = require('assert');

var state = {
    db: null
};

exports.ObjectId = require('mongodb').ObjectId;

exports.connect = function(url, done) {
    if (state.db) return done();

    MongoClient.connect(url, function(err, db) {
        if (err) return done(err);
        state.db = db;
        done();
    })
};

exports.get = function() {
    return state.db;
};

exports.close = function() {
    if (state.db) {
        state.db.close(function(err, result) {
            assert.equal(err, null);
            state.db = null;
            state.mode = null;
        })
    }
};