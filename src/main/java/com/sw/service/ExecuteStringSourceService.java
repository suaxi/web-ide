package com.sw.service;

import com.sw.compile.StringSourceCompiler;
import com.sw.utils.JavaClassExecutor;
import org.springframework.stereotype.Service;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Wang Hao
 * @date 2023/7/3 20:28
 * @description
 */
@Service
public class ExecuteStringSourceService {

    /**
     * 运行时间限制
     */
    private static final int RUN_TIME_LIMITED = 15;

    private static final ExecutorService executorService = new ThreadPoolExecutor(3, 3, 0L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(3));

    public String execute(String source, String systemIn) {
        //编译结果收集器
        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
        byte[] classBytes = StringSourceCompiler.compile(source, collector);

        //编译不通过，获取并返回错误信息
        if (classBytes == null) {
            List<Diagnostic<? extends JavaFileObject>> compileError = collector.getDiagnostics();
            StringBuilder result = new StringBuilder();
            for (Diagnostic<? extends JavaFileObject> diagnostic : compileError) {
                result.append("Compilation error at ");
                result.append(diagnostic.getLineNumber());
                result.append(".");
                result.append(System.lineSeparator());
            }
            return result.toString();
        }

        //运行字节码的main方法
        Callable<String> task = () -> JavaClassExecutor.execute(classBytes, systemIn);

        Future<String> res;
        try {
            res = executorService.submit(task);
        } catch (RejectedExecutionException e) {
            return "服务器忙，请稍后再试！";
        }

        //运行结果
        String runResult;
        try {
            runResult = res.get(RUN_TIME_LIMITED, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            runResult = "Program interrupted";
        } catch (ExecutionException e) {
            e.printStackTrace();
            runResult = e.getCause().getMessage();
        } catch (TimeoutException e) {
            runResult = "Time Limit Exceeded.";
        }
        return runResult;
    }
}
