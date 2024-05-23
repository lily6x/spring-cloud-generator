import lombok.Data;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Generator {

    //project 根目录
    public final static String BASE_PATH = "D:/Lily/work/project";
//    public final static String BASE_PATH = "D://lily/temp";
    public final static String TEMP_PATH = "D://lily/temp/plus";
    public final static String AUTHOR = "Lily";
    public final static String PATH_API = "api";
    public final static String PATH_CLIENT = "client";
    public final static String PATH_PROVIDER = "provider";

    public final static GeneratorConf generatorConf = null;


    public static void generatorCus(String outputFilePath, String templatePath, VelocityContext context){
        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADERS, "classpath");
        Velocity.setProperty("resource.loader.classpath.class", ClasspathResourceLoader.class.getName());
        // 初始化Velocity
        Velocity.init();
        // 创建Velocity上下文
//        VelocityContext context = new VelocityContext();
//        context.put("message", "Hello, Velocity!");
        // 获取模板文件
        //Template template = Velocity.getTemplate("templates/parent.pom.vm", "UTF-8");
        Template template = Velocity.getTemplate(templatePath, "UTF-8");

        // 准备输出文件
//        String outputFilePath = "D://lily/temp/parent.pom";

        try (Writer writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            // 将模板和数据合并到输出文件
            template.merge(context, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("File generated successfully at: " + outputFilePath);
    }

    public static void createDirectory(String directoryPath){
        // 指定要创建的目录路径
//        String directoryPath = "/path/to/your/directory";

        // 创建File对象
        File directory = new File(directoryPath);

        // 使用mkdirs()方法创建目录
        boolean created = directory.mkdirs();

        if (created) {
            System.out.println("目录已成功创建！");
        } else {
            System.out.println("目录创建失败，可能是由于某些原因，比如路径无效或权限不足。");
        }
    }

    @Data
    public static class GeneratorConf{

        private String groupId;
        private String artifactId;
        private String model;
        private String projectPath;
        private String modelApiPath;
        private String modelClientPath;
        private String modelProviderPath;

        private String modelApi;
        private String modelClient;
        private String modelProvider;

        private String dbUrl;
        private String dbUserName;
        private String dbPassword;
        private String dbSchema;

        private String include;



        public String getModelApi() {
            return model+"-api";
        }

        public String getModelClient() {
            return model+"-client";
        }

        public String getModelProvider() {
            return model+"-provider";
        }

        public String getProjectPath(){
            return String.format("%s/%s", BASE_PATH,this.artifactId);
        }
        public String getModelApiPath(){
//            String projectPath = Generator.BASE_PATH + "/" + artifactId;
            return String.format("%s/%s/%s-%s",BASE_PATH,this.artifactId,this.model,PATH_API);
        }
        public String getModelClientPath(){
            return String.format("%s/%s/%s-%s",BASE_PATH,this.artifactId,this.model,PATH_CLIENT);
        }
        public String getModelProviderPath(){
            return String.format("%s/%s/%s-%s",BASE_PATH,this.artifactId,this.model,PATH_PROVIDER);
        }

    }

    /**
     * 创建项目基础目录
     */
    public static Map<String,String> createBaseDirectory(String artifactId, String model){

        //project path
        String groupId = "com.lily.user";
//        String artifactId = "user-center";
//        String model = "user";
        String projectPath = Generator.BASE_PATH + "/" + artifactId;
        Generator.createDirectory(projectPath);

        String modelApiPath = String.format("%s/%s-%s",projectPath,model,PATH_API);
        String modelClientPath = String.format("%s/%s-%s",projectPath,model,PATH_CLIENT);
        String modelProviderPath = String.format("%s/%s-%s",projectPath,model,PATH_PROVIDER);
        Generator.createDirectory(modelApiPath);
        Generator.createDirectory(modelClientPath);
        Generator.createDirectory(modelProviderPath);
        Map<String,String> paths = new HashMap<>();
        paths.put(PATH_API,modelApiPath);
        paths.put(PATH_CLIENT,modelClientPath);
        paths.put(PATH_PROVIDER,modelProviderPath);
        return paths;
    }

    /**
     * 创建项目基础目录
     */
    public static void createBaseDirectory(GeneratorConf generatorConf){

        Generator.createDirectory(generatorConf.getProjectPath());
        Generator.createDirectory(generatorConf.getModelApiPath());
        Generator.createDirectory(generatorConf.getModelClientPath());
        Generator.createDirectory(generatorConf.getModelProviderPath());

    }


    public static void deleteDirectory(File directory) {
        // 确保传入的文件对象存在并且是一个目录
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles(); // 获取目录下的所有文件和子目录
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // 递归删除子目录
                        deleteDirectory(file);
                    } else {
                        // 删除文件
                        file.delete();
                    }
                }
            }
            // 删除空目录
            directory.delete();
            System.out.println("目录 " + directory.getAbsolutePath() + " 已被删除");
        } else {
            System.out.println("指定的路径不是一个存在的目录");
        }
    }

    public static String mapMysqlTypeToJava(String mysqlType) {
        Map<String, String> typeMap = new HashMap<>();
        typeMap.put("VARCHAR", "String");
        typeMap.put("CHAR", "String");
        typeMap.put("TEXT", "String");
        typeMap.put("INT", "int");
        typeMap.put("BIGINT", "long");
        typeMap.put("FLOAT", "float");
        typeMap.put("DOUBLE", "double");
        typeMap.put("DECIMAL", "BigDecimal"); // 需要导入 java.math.BigDecimal 类
        // 添加其他类型的映射...

        // 如果有映射，则返回对应的Java类型；否则返回默认的Object类型
        return typeMap.getOrDefault(mysqlType.toUpperCase(), "Object");
    }
}
