package com.gradletest.log

import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class LogPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        println('welcome to log inject plugin...')

        // 找到项目中的 某个继承至 BaseExtension 的扩展
        def ext = project.extensions.getByType(BaseExtension)
        // 往该扩展中添加 transform
        // 这里其实就是将我们自定义的这个 transform 添加到了集合中
        ext.registerTransform(new LogsTransform(project))
    }

}