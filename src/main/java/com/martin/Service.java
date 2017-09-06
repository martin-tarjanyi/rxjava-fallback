package com.martin;

import io.reactivex.Observable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

    public class Service
    {
        private static final Queue<Item> itemsProvidedByService = new LinkedList<>(
                Arrays.asList(Item.of("first"), Item.of("second"), Item.of("third"), Item.of("fourth"), Item.of("root")));

        public static Observable<Result> call(Item previousItem)
        {
            Item newItem = itemsProvidedByService.poll();

            if (newItem == null)
            {
                return Observable.just(Result.empty());
            }

            return Observable.just(Result.of(newItem));
        }

        public static Result callDirectly(Item previousItem)
        {
            Item newItem = itemsProvidedByService.poll();

            if (newItem == null)
            {
                return Result.empty();
            }

            return Result.of(newItem);
        }

        public static Observable<Result> call(Queue<Item> todoList)
        {
            Item previousItem = todoList.poll();

            Item newItem = itemsProvidedByService.poll(); // we could pass previousItem to a real service here

            if (newItem == null)
            {
                return Observable.just(Result.empty());
            }

            todoList.add(newItem);

            return Observable.just(Result.of(newItem));
        }
    }
