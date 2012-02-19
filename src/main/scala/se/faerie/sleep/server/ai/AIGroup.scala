package se.faerie.sleep.server.ai
import se.faerie.sleep.common.MapPosition

class AIGroup(val id : Long) {
    var groupTarget: java.lang.Long = null;
    var rallyPoint : MapPosition = null;
    var lastUpdated : Long = Long.MinValue;
}