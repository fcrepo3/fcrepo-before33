// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ComponentVisitor.java

package com.sun.xml.rpc.processor.schema;


// Referenced classes of package com.sun.xml.rpc.processor.schema:
//            AnnotationComponent, AttributeDeclarationComponent, AttributeGroupDefinitionComponent, AttributeUseComponent, 
//            ComplexTypeDefinitionComponent, ElementDeclarationComponent, IdentityConstraintDefinitionComponent, ModelGroupComponent, 
//            ModelGroupDefinitionComponent, NotationDeclarationComponent, ParticleComponent, SimpleTypeDefinitionComponent, 
//            WildcardComponent

public interface ComponentVisitor {

    public abstract void visit(AnnotationComponent annotationcomponent) throws Exception;

    public abstract void visit(AttributeDeclarationComponent attributedeclarationcomponent) throws Exception;

    public abstract void visit(AttributeGroupDefinitionComponent attributegroupdefinitioncomponent) throws Exception;

    public abstract void visit(AttributeUseComponent attributeusecomponent) throws Exception;

    public abstract void visit(ComplexTypeDefinitionComponent complextypedefinitioncomponent) throws Exception;

    public abstract void visit(ElementDeclarationComponent elementdeclarationcomponent) throws Exception;

    public abstract void visit(IdentityConstraintDefinitionComponent identityconstraintdefinitioncomponent) throws Exception;

    public abstract void visit(ModelGroupComponent modelgroupcomponent) throws Exception;

    public abstract void visit(ModelGroupDefinitionComponent modelgroupdefinitioncomponent) throws Exception;

    public abstract void visit(NotationDeclarationComponent notationdeclarationcomponent) throws Exception;

    public abstract void visit(ParticleComponent particlecomponent) throws Exception;

    public abstract void visit(SimpleTypeDefinitionComponent simpletypedefinitioncomponent) throws Exception;

    public abstract void visit(WildcardComponent wildcardcomponent) throws Exception;
}
