package fedora.client.objecteditor;

import fedora.server.types.gen.Datastream;

public interface DatastreamListener {

    public void datastreamAdded(Datastream ds);

    public void datastreamModified(Datastream ds);

    public void datastreamPurged(String dsID);

}