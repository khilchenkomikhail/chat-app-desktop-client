
Работа с клиентом

Все fxml надо сохранять в main/resources/fxmls

Как их нормально открывать можно узнать в демке

Для удобства лучше создать конфигурацию(для дебага пока не придумал, как сделать).
Создание конфигурации:

1. Перейти в Edit Configurations...->Add New Configuration->Maven.
2. Придумать для неё интересное название.
3. Записать в Parameters->Command line javafx:run.
4. (Опционально)Добавить вызов clean перед run. Перейти General->Before launch->Add->Run Maven Goal. Записать в command Line clean:clean.