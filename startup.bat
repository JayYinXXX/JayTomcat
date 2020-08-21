rem 将源码编译后的.class文件打包放入lib目录，通过公共类加载器CommonClassLoader加载使用
rem 将Bootstrap和CommonClassLoader打包，Bootstrap作为入口
rem 通过Bootstrap启动程序
del /q bootstrap.jar
jar cvf0 bootstrap.jar -C out/production/jaytomcat com/jayyin/jaytomcat/Bootstrap.class -C out/production/jaytomcat com/jayyin/jaytomcat/classloader/CommonClassLoader.class
del /q lib/jaytomcat.jar
cd out/production/jaytomcat
jar cvf0 ../../../lib/jaytomcat.jar *
cd ../../..
java -cp bootstrap.jar com/jayyin/jaytomcat.Bootstrap
pause