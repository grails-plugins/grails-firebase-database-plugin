package com.objectcomputing.firebase.database;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.tasks.Task;
import com.google.firebase.tasks.TaskCompletionSource;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;


public class QueryExtension {

    /**
     * Attach a value event listener to this database query object.
     * Supports multiple handlers; not required to implement all handlers.
     * Each handler provided must be identified and provided with its own Closure.
     * Provide return value to removeEventListener to detach the listener.
     *
     * <pre>
     *     def listener = query.addValueEventListener {
     *         onDataChange { DataSnapshot snapshot ->
     *             //...
     *         }
     *         onCancelled { DatabaseError error ->
     *             //...
     *         }
     *     }
     *
     *     //... later...
     *     query.removeEventListener(listener)
     * </pre>
     * @param self Firebase database Query
     * @param closure Groovy Closure
     * @return a Firebase database ValueEventListener
     */
    public static ValueEventListener addValueEventListener(
            Query self,
            @DelegatesTo(ValueEventListenerBuilder.class) Closure closure
    ) {
        return self.addValueEventListener(
                ValueEventListenerBuilder.create(closure)
        );
    }

    /**
     * Attach a child event listener to this database query object.
     * Supports multiple handlers; not required to implement all handlers.
     * Each handler provided must be identified and provided with its own Closure.
     * Provide return value to removeEventListener to detach the listener.
     *
     * <pre>
     *    def listener = query.addChildEventListener {
     *         onChildAdded { DataSnapshot snapshot, String previousChildName ->
     *             //...
     *         }
     *         onChildChanged { DataSnapshot snapshot, String previousChildName ->
     *             //...
     *         }
     *         onChildMoved { DataSnapshot snapshot, String previousChildName ->
     *             //...
     *         }
     *         onChildRemoved { DataSnapshot snapshot ->
     *             //...
     *         }
     *         onCancelled { DatabaseError error ->
     *             //...
     *         }
     *     }
     *
     *     //... later...
     *     query.removeEventListener(listener)
     * </pre>
     * @param self Firebase database Query
     * @param closure Groovy Closure
     * @return a Firebase database ChildEventListener
     */    public static ChildEventListener addChildEventListener(
            Query self,
            @DelegatesTo(ChildEventListenerBuilder.class) Closure closure
    ) {
        return self.addChildEventListener(
                ChildEventListenerBuilder.create(closure)
        );
    }

    /**
     * Attach a value event listener to this database query object.
     * This listener only listens for the onDataChange event.
     * Note the slight name change at this higher level. (onValueChanged)
     * Provide return value to removeEventListener to detach the listener.
     *
     * <pre>
     *     def listener = query.onValueChanged { DataSnapshot snapshot ->
     *         //...
     *     }
     *
     *     //... later...
     *     query.removeEventListener(listener)
     * </pre>
     * @param self Firebase database Query
     * @param closure Groovy closure
     * @return a Firebase database ValueEventListener
     */
    public static ValueEventListener onValueChanged(Query self, Closure closure) {
        return self.addValueEventListener(
                ValueEventListenerBuilder.create().onDataChange(closure).build()
        );
    }

    /**
     * Attach a child event listener to this database query object.
     * This listener only listens for the onChildAdded event.
     * Provide return value to removeEventListener to detach the listener.\
     *
     * @param self Firebase database Query
     * @param closure Groovy closure
     * @return a Firebase database ChildEventListener
     */
    public static ChildEventListener onChildAdded(Query self, Closure closure) {
        return self.addChildEventListener(
                ChildEventListenerBuilder.create().onChildAdded(closure).build()
        );
    }

    /**
     * Attach a child event listener to this database query object.
     * This listener only listens for the onChildChanged event.
     * Provide return value to removeEventListener to detach the listener.\
     *
     * @param self Firebase database Query
     * @param closure Groovy closure
     * @return a Firebase database ChildEventListener
     */
    public static ChildEventListener onChildChanged(Query self, Closure closure) {
        return self.addChildEventListener(
                ChildEventListenerBuilder.create().onChildChanged(closure).build()
        );
    }

    /**
     * Attach a child event listener to this database query object.
     * This listener only listens for the onChildMoved event.
     * Provide return value to removeEventListener to detach the listener.\
     *
     * @param self Firebase database Query
     * @param closure Groovy closure
     * @return a Firebase database ChildEventListener
     */
    public static ChildEventListener onChildMoved(Query self, Closure closure) {
        return self.addChildEventListener(
                ChildEventListenerBuilder.create().onChildMoved(closure).build()
        );
    }

    /**
     * Attach a child event listener to this database query object.
     * This listener only listens for the onChildRemoved event.
     * Provide return value to removeEventListener to detach the listener.\
     *
     * @param self Firebase database Query
     * @param closure Groovy closure
     * @return a Firebase database ChildEventListener
     */
    public static ChildEventListener onChildRemoved(Query self, Closure closure) {
        return self.addChildEventListener(
                ChildEventListenerBuilder.create().onChildRemoved(closure).build()
        );
    }

    /**
     * Get the value of this database query object. One-time read. Asynchronous.
     *
     * @param self Firebase database Query
     * @return a Promise expected to receive the value of the snapshot at this location
     */
    public static Task<Object> getValue(Query self) {
        final TaskCompletionSource<Object> source = new TaskCompletionSource<>();

        self.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        source.setResult(dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        source.setException(databaseError.toException());
                    }
                }
        );

        return source.getTask();
    }

    public static void getValue(Query self, @NotNull final Closure closure) {
        self.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        closure.call(null, dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        closure.call(databaseError.toException(), null);
                    }
                }
        );
    }

    /**
     * Get the value of this database query object. One-time read. Asynchronous.
     *
     * @param self Firebase database Query
     * @param valueType class into which snapshot value should be marshaled
     * @return a Promise expected to receive the value of the snapshot at this location
     */
    public static <T> Task<T> getValue(Query self, final Class<T> valueType) {
        final TaskCompletionSource<T> source = new TaskCompletionSource<>();

        self.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        source.setResult(dataSnapshot.getValue(valueType));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        source.setException(databaseError.toException());
                    }
                }
        );

        return source.getTask();
    }

    public static <T> void getValue(Query self, final Class<T> valueType, @NotNull final Closure closure) {
        self.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        closure.call(null, dataSnapshot.getValue(valueType));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        closure.call(databaseError.toException(), null);
                    }
                }
        );
    }

}