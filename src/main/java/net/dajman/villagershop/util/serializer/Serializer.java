package net.dajman.villagershop.util.serializer;

import java.util.Optional;

public interface Serializer<OBJECT, SERIALIZED> {

    Optional<SERIALIZED> serialize(OBJECT object);

    Optional<OBJECT> deserialize(SERIALIZED serialized) throws Exception;

}
