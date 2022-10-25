package com.example.anno_compiler;

import static com.example.anno_compiler.Consts.ANNOTATION_NAME;
import static com.example.anno_compiler.Consts.KEY_MODULE_NAME;
import static com.example.anno_compiler.Consts.PROVIDER_ANNOTATION_NAME;

import com.alibaba.fastjson.JSON;
import com.example.anno.FragDest;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 * Created by zhaochaoyue on 2020/06/09.
 */

@AutoService(Processor.class)                       //auto register processor
@SupportedOptions({KEY_MODULE_NAME})
@SupportedSourceVersion(SourceVersion.RELEASE_7)    //use java version
@SupportedAnnotationTypes({ANNOTATION_NAME,PROVIDER_ANNOTATION_NAME})          //processor type
public class ComponendInitProcessor extends AbstractProcessor {

    private Filer mFiler;
    private Elements mElements;
    private Logger logger;
    private Types types;
    private String moduleName = null;   // Module name, maybe its 'app' or others

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        mFiler = processingEnvironment.getFiler();
        mElements = processingEnvironment.getElementUtils();
        types = processingEnvironment.getTypeUtils();
        logger = new Logger(processingEnvironment.getMessager());
        if(processingEnvironment.getOptions() != null) {
            moduleName = processingEnvironment.getOptions().get(KEY_MODULE_NAME);
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if(set != null && !set.isEmpty()) {
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(FragDest.class);
            try {
                logger.info(">>> Found Processor start ...<<<");
                parseProcessor(elements);
            } catch (IOException e) {
                logger.error(e);
            }
            return true;
        }
        return false;
    }

    private void parseProcessor(Set<? extends Element> elements) throws IOException {
        if(elements != null && !elements.isEmpty()) {
            logger.info(">>> Found processor, size is " + elements.size() + "<<<");
            ArrayList<FragDestBean> beans = new ArrayList<>();
            for (Element element : elements) {
                TypeElement typeElement = (TypeElement) element;
                FragDest fragDest = typeElement.getAnnotation(FragDest.class);
                FragDestBean bean = new FragDestBean();
                bean.setClazName(typeElement.getQualifiedName().toString());
                bean.setPageUri(fragDest.pageUri());
                bean.setStarter(fragDest.isStarter());
                beans.add(bean);
            }
            String content = JSON.toJSONString(beans);
            FileObject resource = mFiler.createResource(StandardLocation.CLASS_OUTPUT, "", "temp.json");
            String resourcePath = resource.toUri().getPath();
            logger.info("resourcePath:" + resourcePath);
            Writer writer = resource.openWriter();
            writer.append(content);
            writer.flush();
            writer.close();
            logger.info(">>> Generated InitImpl finish <<< ");
        }
    }

    private boolean isConcreteSubType(Element element, String className) {
        return isConcreteType(element) && isSubType(element, className);
    }

    //check Not-ABSTRACT
    private boolean isConcreteType(Element element) {
        return element instanceof TypeElement && !element.getModifiers().contains(Modifier.ABSTRACT);
    }

    private boolean isSubType(Element element, String className) {
        return element != null && isSubType(element.asType(), className);
    }

    private boolean isSubType(TypeMirror type, String className) {
        return type != null && types.isSubtype(type, typeMirror(className));
    }

    //String --> TypeElement
    private TypeElement typeElement(String className) {
        return mElements.getTypeElement(className);
    }

    //String --> TypeMirror
    private TypeMirror typeMirror(String className) {
        return typeElement(className).asType();
    }
}
