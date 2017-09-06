package com.martin;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;

/**
 * Hello world!
 */
public class App
{
    private static final Queue<Item> items = new LinkedList<>(
            Arrays.asList(Item.of("first"), Item.of("second"), Item.of("third"), Item.of("fourth"), Item.of("root")));

    public static void main(String[] args)
    {
        Result result = Observable.just(new Item("start"))
                                  .map(Result::of)
                                  .flatMap(App::fallbackUntilIsRoot)
                                  .blockingSingle();

        System.out.println(result);
    }

    private static ObservableSource<Result> fallbackUntilIsRoot(Result result)
    {
        if (result.isRoot())
        {
            return Observable.just(result);
        } else
        {
            return Observable.fromCallable(() -> callDependency(result.getItem()))
                             .onErrorReturn(e -> Result.ofRoot(result.getItem()))
                             .flatMap(App::fallbackUntilIsRoot); // recursion
        }
    }

    private static Result callDependency(Item previousItem)
    {
        //        throw new RuntimeException("service failed"); //to test error
        Item newItem = items.poll(); //we could pass previousItem to a real service here

        if (newItem == null) // we have no more ancestors
        {
            return Result.ofRoot(previousItem);
        }

        return Result.of(newItem);
    }

    private static class Result
    {
        private final Item item;
        private final boolean isRoot;

        private Result(Item item, boolean isRoot)
        {
            this.item = item;
            this.isRoot = isRoot;
        }

        private static Result of(Item item)
        {
            return new Result(item, false);
        }

        private static Result ofRoot(Item item)
        {
            return new Result(item, true);
        }

        Item getItem()
        {
            return item;
        }

        boolean isRoot()
        {
            return isRoot;
        }

        @Override
        public String toString()
        {
            return "Result{" +
                    "item=" + item +
                    ", isRoot=" + isRoot +
                    '}';
        }
    }

    private static class Item
    {
        private final String value;

        private Item(String value)
        {
            this.value = value;
        }

        private static Item of(String value)
        {
            return new Item(value);
        }

        public String getValue()
        {
            return value;
        }

        @Override
        public String toString()
        {
            return "Item{" +
                    "value='" + value + '\'' +
                    '}';
        }
    }
}
