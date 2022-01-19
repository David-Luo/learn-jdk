MapperRenderingProcessor#createSourceFile()


AbstractProcessor#process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnvironment) 
查找被注解的元素(类)
Set<? extends Element> annotatedMappers = roundEnvironment.getElementsAnnotatedWith( annotation );
                



org.mapstruct.ap.internal.processor.CdiComponentProcessor<Mapper, Mapper>1100
org.mapstruct.ap.internal.processor.Jsr330ComponentProcessor<Mapper, Mapper>1100
org.mapstruct.ap.internal.processor.MapperCreationProcessor<List<SourceMethod>, Mapper>1000
org.mapstruct.ap.internal.processor.MapperRenderingProcessor<Mapper, Mapper>1100
org.mapstruct.ap.internal.processor.MethodRetrievalProcessor<Void, List<SourceMethod>> 1
org.mapstruct.ap.internal.processor.SpringComponentProcessor<Mapper, Mapper>
org.mapstruct.ap.internal.processor.MapperServiceProcessor<Mapper, Void> 10000



ConversionMethod.ftl



    /**
     * Wrap a {@code Writer} returned from the {@code JavaFileManager}
     * to properly register source or class files when they are
     * closed.
     */
    private class FilerWriter extends FilterWriter