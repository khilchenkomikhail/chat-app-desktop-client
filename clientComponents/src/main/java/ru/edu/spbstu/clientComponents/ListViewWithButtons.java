package ru.edu.spbstu.clientComponents;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListViewWithButtons<T> extends ListView<HBoxCell<T>> {
    public ListViewWithButtons()
    {
        super();
    }
    private final EventHandler<ActionEvent> handler=new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent arg0) {
            int i1 =((HBoxCell.MyButton)arg0.getSource()).structNum;

            //System.out.println(i1);
            update(i1);
        }
    };
    private List<T> lst=new ArrayList<T>(0);
    public void setItem(List<T> arr)
    {
        List<HBoxCell<T>> list = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {

            list.add(new HBoxCell<T>(arr.get(i), "x",i));

        }


        for (HBoxCell<T> var : list) {
            var.button.setOnAction(handler);
        }


        ObservableList<HBoxCell<T>> myObservableList = FXCollections.observableList(list);
        this.setItems(myObservableList);
    }
    public void resetList(List<T> arr)
    {
        this.lst=arr;
        List<HBoxCell<T>> list = new ArrayList<>();
        for ( int i=0;i<lst.size();i++)
        {
            HBoxCell<T> temp=new HBoxCell<T>(lst.get(i),"x",i);
            temp.button.setOnAction(handler);
            list.add(temp);
        }

        this.setItems(FXCollections.observableList(list));
    }
    public void addInList(T element)
    {
        this.lst.add(element);
        var list2=this.getItems();
        HBoxCell<T>temp=new HBoxCell<T>(element,"x",this.lst.size()-1);
        temp.button.setOnAction(handler);
        list2.add(temp);
        this.setItems(list2);

       /* List<T> lst2=new ArrayList<T>(lst);
        lst2.add(element);*/

    }
    public List<T> getList()
    {
        return new ArrayList<T>(lst);
    }
    public void init()
    {
        List<HBoxCell<T>> list = new ArrayList<>();
        for (int i = 0; i < 12; i++) {

            list.add(new HBoxCell("Item " + i, "Delete user " + (i-10),i));
        }


        var array=this.getItems();
        if(array.size()<1) {
            for (HBoxCell var : list) {
                var.button.setOnAction(handler);
            }


            ObservableList<HBoxCell<T>> myObservableList = FXCollections.observableList(list);
            this.setItems(myObservableList);
            return;
        }
        for (HBoxCell var : array) {
            var.button.setOnAction(handler);
        }
        this.setItems(array);


    }
   private void  update(int index)
   {

       ObservableList<HBoxCell<T>> myObservableList = FXCollections.observableList(this.getItems());
       if(lst.size()==1)
       {
           lst=new ArrayList<>();
           myObservableList=FXCollections.observableList(new ArrayList<>(0));

       }
       else {
           myObservableList.remove(index);
           lst.remove(index);
       }
       for (int i = index; i<myObservableList.size(); i++)
       {
           HBoxCell temp= myObservableList.get(i);
           temp.button.structNum-=1;
           myObservableList.set(i,temp);
       }
       this.setItems(myObservableList);
   }
}
