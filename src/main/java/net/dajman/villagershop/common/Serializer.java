package net.dajman.villagershop.common;

import java.util.Optional;

public interface Serializer<OBJECT, SERIALIZED> {

    Optional<SERIALIZED> serialize(OBJECT object);

    Optional<OBJECT> deserialize(SERIALIZED serialized);

}
