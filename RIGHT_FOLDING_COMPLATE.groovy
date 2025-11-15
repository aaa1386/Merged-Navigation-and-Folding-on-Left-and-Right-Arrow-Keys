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
            
            if (isRootOnLeft) {
                // اگر ریشه سمت چپ است → همیشه به اولین فرزند برو
                c.select(firstChild)
            } else {
                // اگر ریشه سمت راست است
                // بررسی وضعیت فولد فرزندان
                def allFolded = root.children.every { it.folded }
                
                if (!allFolded) {
                    // اگر بعضی فرزندان آنفولد هستند → همه را فولد کن
                    root.children.each { child ->
                        child.folded = true
                    }
                }
                // اگر همه فولد هستند → هیچ کاری نکن
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
if (!isParentTopOrLeft) {
    if (!isEffectivelyFolded) {
        // اگر آنفولد است و فرزند دارد → به اولین فرزند برو
        if (hasChildren) {
            c.select(current.children[0])
        }
    } else {
        // اگر فولد است یا بدون فرزند → آنفولد کن و اگر فرزند دارد به اولین فرزند برو
        if (hasChildren) {
            current.folded = false
            c.select(current.children[0])
        }
    }
} 
// اگر والد در سمت چپ باشد
else {
    if (isEffectivelyFolded) {
        // اگر فولد است یا بدون فرزند → والد را انتخاب کن و والد را فولد کن
        def parent = current.parent
        if (parent != null) {
            parent.folded = true  // این خط اضافه شد
            c.select(parent)
        }
    } else {
        // اگر آنفولد است و فرزند دارد → فولد کن
        current.folded = true
    }
}