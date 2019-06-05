/*
    data structures
*/

/*
    history

    2018/04/18
*/

var sub = exports.sub = function(connectionID, subID,x, y, AoI, channel, name) {
    //this.layer = layer;
    this.x = x || 0;
    this.y = y || 0;
    this.AoI = AoI || 0;
    this.subID = subID;
    this.connectionID = connectionID;
    this.channel = channel || "";
    this.name = name || "";

    this.evaluate = function(x,y,AoI) {
      if (this.x == x && this.y == y && this.AoI != AoI) {
        return 1;
      }
      if (this.x==x && this.y==y && this.AoI == AoI) {
        return 2
      }
      return 0;
    }

    this.toString = function(){
        return "Subscription ID: "+ this.subID + " Connection ID: " + this.connectionID + " <" + this.x + "," + this.y + "> with radius " + this.AoI + " on channel " + this.channel;
    }
}
