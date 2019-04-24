package datamodel

class Property {

    final String name
    String type = "string"; // default
    String instance_of;

    Property(String dgp_name) {
        this.name = dgp_name
    }

    Property as_number() {
        this.type = "number"
        return this
    }

    Property as_date() {
        this.type = "date"
        return this
    }

    Property instance_of(dg_name) {
        this.instance_of = dg_name
        return this
    }

}


