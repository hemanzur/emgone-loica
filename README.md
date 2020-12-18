EMG One
=======

Aplicación Android
---------------------------
Fue implementada usando el framework de desarrollo de juegos multiplataforma [libGDX](https://libgdx.badlogicgames.com/)

Al ser multiplataforma, la implementación está en la carpeta `core`. 

- `buttons` contiene clases que implementan botones que se utilizan en la vista del juego mismo. 

- `main` contiene la clase `MainGame.java`, la cual inicia el juego

- `screens` contiene todas las distintas vistas del juego. Una por clase. En particular el juego mismo está implementado en la clase `EmgOneGame.java`

- `util` contiene clases utilitarias, como `Constants.java`, que es un conjunto de constantes transversales al juego (los endpoints al servidor por ejemplo). También contiene la clase `MicProcessor.java`, que implementa el algoritmo de procesamiento de la señal. La clase `GamePreferences.java` manipula las configuraciones y preferencias de cada juego, incluyendo los valores de la calibración. También guarda los datos de usuario, incluyendo el token necesario para los requests al servidor. Estos datos son guardados en un archivo local.




