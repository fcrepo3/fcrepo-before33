package fedora.client;

import java.io.File;

public class InlineDataStream 
        extends DataStream {
        
    public InlineDataStream(File tempDir, String id) {
        super(tempDir, id);
    }
        
    public final int getType() {
        return DataStream.INLINE;
    }

}