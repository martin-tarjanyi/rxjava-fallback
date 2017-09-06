package com.martin;

import io.reactivex.Emitter;
import io.reactivex.Observable;
import io.reactivex.functions.Predicate;
import rx.observables.SyncOnSubscribe;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Hello world!
 */
public class App
{
    public static void main(String[] args)
    {
        //        solutionWithRecursion();
        //        solutionWithRangeConcatMapAndExternalQueue();

                solutionWithGenerate();

        //        solutionWithRepeat();
        //        solutionWIthRxJava1SyncOnSubscribe();
    }

    private static void solutionWithGenerate()
    {
        Result startElement = Result.of(Item.of("start"));

        Result result = Observable.generate(() -> startElement, App::generate)
                                  .startWith(startElement)
                                  .lastElement()
                                  .blockingGet();

        System.out.println(result);
    }

    private static Result generate(Result previousResult, Emitter<Result> emitter)
    {
        Result newResult = Service.callDirectly(previousResult.getItem());

        if (newResult.isEmpty())
        {
            //            emitter.onNext(previousResult);
            emitter.onComplete();
        } else
        {
            emitter.onNext(newResult);
        }

        return newResult;
    }

    private static void solutionWIthRxJava1SyncOnSubscribe()
    {
        Result start = Result.of(Item.of("start"));

        Result last = rx.Observable
                .create(SyncOnSubscribe.<Result, Result>createStateful(() -> start, (previous, subscriber) -> {
                    Result result = Service.callDirectly(previous.getItem());
                    if (result.isEmpty())
                    {
                        subscriber.onCompleted();
                        return result;
                    }

                    subscriber.onNext(result);
                    return result;
                }))
                .startWith(start)
                .toBlocking()
                .last();

        System.out.println(last);
    }

    private static void solutionWithRecursion()
    {

        Result result = Observable.just(Item.of("start"))
                                  .map(Result::of)
                                  .flatMap(App::fallbackUntilIsRoot)
                                  .blockingSingle();

        System.out.println(result);
    }

    private static void solutionWithRangeConcatMapAndExternalQueue()
    {
        Queue<Item> todoList = new LinkedList<>(Collections.singleton(Item.of("start")));

        Result result = Observable.range(1, Integer.MAX_VALUE) //max tries
                                  .concatMap(attempt -> Service.call(todoList))
                                  .takeUntil(Result::isEmpty)
                                  .takeLast(2)
                                  .firstElement()
                                  .blockingGet();

        System.out.println(result);
    }

    private static void solutionWithRepeat()
    {
        Queue<Result> todoList = new LinkedList<>(Collections.singleton(Result.of(Item.of("start"))));

        Result result = Observable.fromCallable(todoList::poll)
                                  .map(oldResult -> {
                                      Result newResult = Service.callDirectly(oldResult.getItem());
                                      todoList.add(newResult);
                                      return newResult;
                                  })
                                  .repeat()
                                  .takeUntil((Predicate<? super Result>) Result::isEmpty)
                                  .takeLast(2)
                                  .first(Result.of(Item.of("Well, something went wrong.")))
                                  .blockingGet();

        System.out.println(result);
    }

    private static Observable<Result> fallbackUntilIsRoot(Result previousResult)
    {
        if (previousResult.isRoot())
        {
            return Observable.just(previousResult);
        } else
        {
            return Service.call(previousResult.getItem())
                          .onErrorReturn(e -> Result.ofRoot(previousResult.getItem()))
                          .map(result -> result.isEmpty() ? Result.ofRoot(previousResult.getItem()) : result)
                          .flatMap(App::fallbackUntilIsRoot); // recursion
        }
    }
}
