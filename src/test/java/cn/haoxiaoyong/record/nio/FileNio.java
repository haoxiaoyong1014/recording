package cn.haoxiaoyong.record.nio;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by haoxy on 2019/2/15.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class FileNio {

    //往本地文件中写数据
    @Test
    public void test1() throws Exception {
        String str = "你好!,我是谁谁谁";
        FileOutputStream fos = new FileOutputStream("basic.txt");
        FileChannel fc = fos.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put(str.getBytes());
        byteBuffer.flip();
        fc.write(byteBuffer);
        fos.close();
        fc.close();
    }

    //从本地文件中读数据
    @Test
    public void test2() throws Exception{
        File file=new File("basic.txt");
        FileInputStream fis=new FileInputStream(file);
        FileChannel fc = fis.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length());
        fc.read(byteBuffer);
        System.out.println(new String(byteBuffer.array()).trim());
        fis.close();
        fc.close();

    }
}
