package com.goshoppi.pos.architecture.repository.PurchaseOrderRepo

import com.goshoppi.pos.architecture.dao.PurchaseOrderDao
import javax.inject.Inject

class PurchaseOrderRepositoryImp @Inject constructor(var pODao: PurchaseOrderDao):PurchaseOrderRepository{

}