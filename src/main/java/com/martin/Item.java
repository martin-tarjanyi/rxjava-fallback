package com.martin;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
class Item
{
    private final String value;

    Item(String value)
    {
        this.value = value;
    }

    public static Item of(String value)
    {
        return new Item(value);
    }

    public String getValue()
    {
        return value;
    }
}
