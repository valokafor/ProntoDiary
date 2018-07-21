package com.okason.diary.data;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class Migration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

        // Access the Realm schema in order to create, modify or delete classes and their fields.
        RealmSchema schema = realm.getSchema();

        if (oldVersion == 1){
            // Create a new class
            RealmObjectSchema locationSchema = schema.create("Location")
                    .addField("id", String.class, FieldAttribute.PRIMARY_KEY)
                    .addField("address", String.class)
                    .addField("dateCreated", long.class)
                    .addField("dateModified", long.class)
                    .addRealmListField("journals", schema.get("Journal"))
                    .addRealmListField("tasks", schema.get("ProntoTask"));

            schema.get("ProntoTask")
                    .addRealmObjectField("location", schema.get("Location"));

            oldVersion++;
        }

        if (oldVersion == 2){
            schema.get("Location")
                    .addField("name", String.class);
        }

    }


    public int hashCode() {
        return Migration.class.hashCode();
    }

    public boolean equals(Object object) {
        if(object == null) {
            return false;
        }
        return object instanceof Migration;
    }
}
