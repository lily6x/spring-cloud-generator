package com.lily.generator;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class EngineVelocityTemplateEngine  extends VelocityTemplateEngine {

    public static String MODEL = "user333";
    public static String GROUP_ID = "user333";

    @Override
    protected void outputCustomFile(@NotNull List<CustomFile> customFiles, @NotNull TableInfo tableInfo, @NotNull Map<String, Object> objectMap) {
        System.out.println("执行了 自定义模板输出");
        String entityName = tableInfo.getEntityName();
        String parentPath = this.getPathInfo(OutputFile.parent);
        customFiles.forEach((file) -> {
            String filePath = StringUtils.isNotBlank(file.getFilePath()) ? file.getFilePath() : parentPath;
            if (StringUtils.isNotBlank(file.getPackageName())) {
                filePath = filePath + File.separator + file.getPackageName().replaceAll("\\.", "\\" + File.separator);
            }
            objectMap.put("customInfo", file);
            objectMap.put("model", MODEL);
            objectMap.put("groupId", GROUP_ID);
            Function<TableInfo, String> formatNameFunction = file.getFormatNameFunction();
            String fileName = filePath + File.separator + (null != formatNameFunction ? (String)formatNameFunction.apply(tableInfo) : entityName) + file.getFileName();
            this.outputFile(new File(fileName), objectMap, file.getTemplatePath(), file.isFileOverride());
        });
    }
}
