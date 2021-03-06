
var Muon = require("muon-core")
var RS = require("muon-stack-reactive-streams")
var muonurl = process.env.MUON_URL || "amqp://muon:microservices@localhost"

require("muon-amqp").attach(Muon)

logger.info("Muon is enabled, booting up using url " + muonurl)

var muon = Muon.create("hello-world-node", muonurl);

RS.create(muon)

muon.subscribe("stream://hello-world-jvm/ticktock",{},
    function(data) {
        console.dir("Data..." + JSON.stringify(data))
    },
    function(error) {
        logger.error("Errored... now change the stream name to 'ticktock'")
        console.dir(error)
    },
    function() {
        logger.warn("Stream Completed")
    }
)

