package com.martin;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
class Result
{
    private final Item item;
    private final boolean isRoot;

    private Result(Item item, boolean isRoot)
    {
        this.item = item;
        this.isRoot = isRoot;
    }

    public static Result of(Item item)
    {
        return new Result(item, false);
    }

    public static Result ofRoot(Item item)
    {
        return new Result(item, true);
    }

    public static Result empty()
    {
        return new Result(null, false);
    }

    public boolean isEmpty()
    {
        return item == null;
    }

    Item getItem()
    {
        return item;
    }

    boolean isRoot()
    {
        return isRoot;
    }
}
