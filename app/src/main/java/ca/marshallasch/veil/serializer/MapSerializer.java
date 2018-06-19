package ca.marshallasch.veil.serializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.marshallasch.veil.proto.DhtProto;

/**
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-06-18
 */
public class MapSerializer
{

    // this class can not be instantiated
    private MapSerializer() {}

    /**
     * Save the map to a file so it can be recreated
     * @param map the map to save
     * @throws IOException exception thrown if the write fails
     */
    public static void write(OutputStream outputStream, HashMap<String, List<DhtProto.DhtWrapper>> map) throws IOException
    {

        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        Set<Map.Entry<String, List<DhtProto.DhtWrapper>>> set =  map.entrySet();

        // write the set size
        dataOutputStream.writeInt(set.size());

        for(Map.Entry<String, List<DhtProto.DhtWrapper>> entry: set){

            String key = entry.getKey();

            // write the size of the key
            dataOutputStream.writeUTF(key);

            List<DhtProto.DhtWrapper> values = entry.getValue();

            // write the number of values for the key
            dataOutputStream.writeInt(values.size());

            for(DhtProto.DhtWrapper wrapper: values) {
                wrapper.writeTo(dataOutputStream);
            }
        }

        dataOutputStream.flush();
        dataOutputStream.close();
    }

    public static HashMap<String, List<DhtProto.DhtWrapper>> readMap(InputStream inputStream) throws IOException
    {
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        HashMap<String, List<DhtProto.DhtWrapper>> map = new HashMap<>();


        int numKeys = dataInputStream.readInt();
        int numEntries;

        String key;

        List<DhtProto.DhtWrapper> entries;

        DhtProto.DhtWrapper wrapper;

        for (int i = 0; i < numKeys; i++) {

            key = dataInputStream.readUTF();
            numEntries = dataInputStream.readInt();

            entries = new ArrayList<>();

            for(int j = 0; j < numEntries; j++) {

                wrapper = DhtProto.DhtWrapper.parseFrom(dataInputStream);
                entries.add(wrapper);
            }

            map.put(key, entries);
        }

        dataInputStream.close();

        return map;
    }

}
