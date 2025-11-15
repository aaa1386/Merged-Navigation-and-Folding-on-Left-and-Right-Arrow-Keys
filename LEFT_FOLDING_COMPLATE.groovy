import org.freeplane.view.swing.map.NodeView

def current = node
if (!current) return

def root = current.map.root

// اگر روی ریشه هستیم
if (current == root) {
    def hasChildren = root.children && !root.children.isEmpty()
    if (hasChildren) {
        def firstChild = root.children[0]
        def firstChildView = firstChild.delegate.viewers.find { it instanceof NodeView }
        
        if (firstChildView) {
            // تشخیص جهت ریشه
            boolean isRootOnLeft = !firstChildView.isTopOrLeft()
            
            // بررسی وضعیت فولد فرزندان
            def allFolded = root.children.every { it.folded }
            
            if (isRootOnLeft) {
                // اگر ریشه سمت چپ است
                if (!allFolded) {
                    // اگر بعضی فرزندان آنفولد هستند → همه را فولد کن
                    root.children.each { child ->
                        child.folded = true
                    }
                }
                // اگر همه فولد هستند → هیچ کاری نکن
            } else {
                // اگر ریشه سمت راست است → همیشه به اولین فرزند برو
                c.select(firstChild)
            }
        }
    }
    return
}

// برای گره‌های غیر ریشه
def nodeView = current.delegate.viewers.find { it instanceof NodeView }
if (!nodeView?.parentView) return

def hasChildren = current.children && !current.children.isEmpty()
def isEffectivelyFolded = current.folded || !hasChildren
def isParentTopOrLeft = nodeView.isTopOrLeft()

// اگر والد در سمت راست باشد
if (isParentTopOrLeft) {
    if (hasChildren) {
        if (!isEffectivelyFolded) {
            // اگر آنفولد است → به اولین فرزند برو
            c.select(current.children[0])
        } else {
            // اگر فولد است → آنفولد کن و به اولین فرزند برو
            current.folded = false
            c.select(current.children[0])
        }
    }
} 
// اگر والد در سمت چپ باشد
else {
    if (!isEffectivelyFolded) {
        // اگر آنفولد است → فولد کن
        current.folded = true
    } else {
        // اگر فولد است → به والد برو و آن را فولد کن
        def parent = current.parent
        if (parent != null) {
            parent.folded = true
            c.select(parent)
        }
    }
}