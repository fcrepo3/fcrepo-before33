// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ModelGroupComponent.java

package com.sun.xml.rpc.processor.schema;

import java.util.*;

// Referenced classes of package com.sun.xml.rpc.processor.schema:
//            Component, ComponentVisitor, Symbol, AnnotationComponent, 
//            ParticleComponent

public class ModelGroupComponent extends Component {

    private Symbol _compositor;
    private List _particles;
    private AnnotationComponent _annotation;

    public ModelGroupComponent() {
        _particles = new ArrayList();
    }

    public Symbol getCompositor() {
        return _compositor;
    }

    public void setCompositor(Symbol s) {
        _compositor = s;
    }

    public Iterator particles() {
        return _particles.iterator();
    }

    public void addParticle(ParticleComponent c) {
        _particles.add(c);
    }

    public void accept(ComponentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
