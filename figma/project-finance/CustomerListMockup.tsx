import React from 'react';
import { Search, Plus, Mail, Phone, Building2, ArrowLeft, MoreVertical } from 'lucide-react';

export default function CustomerListMockup() {
  const customers = [
    { name: 'Nguyễn Văn A', company: 'ABC Corporation', email: 'nguyenvana@abc.com', phone: '0901234567', status: 'Active' },
    { name: 'Trần Thị B', company: 'XYZ Ltd.', email: 'tranthib@xyz.com', phone: '0909876543', status: 'Active' },
    { name: 'Lê Văn C', company: 'Tech Solutions', email: 'levanc@tech.com', phone: '0912345678', status: 'Inactive' },
    { name: 'Phạm Thị D', company: 'Digital Agency', email: 'phamthid@digital.com', phone: '0923456789', status: 'Active' },
  ];

  return (
    <div className="w-[390px] h-[844px] bg-[#F5F5F5] rounded-3xl shadow-2xl overflow-hidden flex flex-col">
      {/* Status Bar */}
      <div className="h-11 bg-[#2196F3] flex items-center justify-between px-6 text-white text-xs">
        <span>9:41</span>
        <div className="flex gap-1 items-center">
          <div className="w-4 h-3 border border-white rounded-sm" />
          <div className="w-1 h-3 bg-white rounded-sm" />
        </div>
      </div>

      {/* App Bar */}
      <div className="bg-[#2196F3] px-4 py-4 text-white">
        <div className="flex items-center justify-between mb-4">
          <ArrowLeft className="w-6 h-6" />
          <span>Quản lý khách hàng</span>
          <div className="w-6 h-6" />
        </div>
        
        {/* Search Bar */}
        <div className="bg-white/20 rounded-lg px-4 py-2 flex items-center gap-2">
          <Search className="w-5 h-5" />
          <input
            type="text"
            placeholder="Tìm kiếm khách hàng..."
            className="flex-1 bg-transparent outline-none placeholder:text-white/70"
            disabled
          />
        </div>
      </div>

      {/* Content */}
      <div className="flex-1 overflow-auto p-4">
        <div className="space-y-3">
          {customers.map((customer, index) => (
            <div key={index} className="bg-white rounded-xl p-4 shadow">
              <div className="flex items-start gap-3">
                {/* Avatar */}
                <div className="w-12 h-12 bg-gradient-to-br from-[#2196F3] to-[#1976D2] rounded-full flex items-center justify-center text-white flex-shrink-0">
                  {customer.name.charAt(0)}
                </div>

                {/* Info */}
                <div className="flex-1 min-w-0">
                  <div className="flex items-start justify-between mb-1">
                    <div className="text-slate-900">{customer.name}</div>
                    <div className={`px-2 py-0.5 rounded text-xs ${
                      customer.status === 'Active'
                        ? 'bg-[#4CAF50]/10 text-[#4CAF50]'
                        : 'bg-slate-200 text-slate-500'
                    }`}>
                      {customer.status}
                    </div>
                  </div>

                  <div className="flex items-center gap-1 text-xs text-slate-500 mb-1">
                    <Building2 className="w-3 h-3" />
                    <span className="truncate">{customer.company}</span>
                  </div>

                  <div className="flex items-center gap-1 text-xs text-slate-500 mb-1">
                    <Mail className="w-3 h-3" />
                    <span className="truncate">{customer.email}</span>
                  </div>

                  <div className="flex items-center gap-1 text-xs text-slate-500">
                    <Phone className="w-3 h-3" />
                    <span>{customer.phone}</span>
                  </div>
                </div>

                {/* Actions */}
                <button className="text-slate-400">
                  <MoreVertical className="w-5 h-5" />
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* FAB */}
      <div className="absolute bottom-20 right-8">
        <button className="w-14 h-14 bg-[#2196F3] rounded-full shadow-lg flex items-center justify-center text-white">
          <Plus className="w-6 h-6" />
        </button>
      </div>

      {/* Bottom Navigation */}
      <div className="bg-white border-t border-slate-200 px-4 py-3 flex justify-around">
        <button className="flex flex-col items-center gap-1 text-slate-400">
          <div className="w-6 h-6 flex items-center justify-center">
            <div className="w-2 h-2 bg-slate-400 rounded-full" />
          </div>
          <span className="text-xs">Dashboard</span>
        </button>
        <button className="flex flex-col items-center gap-1 text-slate-400">
          <Building2 className="w-6 h-6" />
          <span className="text-xs">Dự án</span>
        </button>
        <button className="flex flex-col items-center gap-1 text-slate-400">
          <Mail className="w-6 h-6" />
          <span className="text-xs">Doanh thu</span>
        </button>
        <button className="flex flex-col items-center gap-1 text-slate-400">
          <Phone className="w-6 h-6" />
          <span className="text-xs">Chi phí</span>
        </button>
        <button className="flex flex-col items-center gap-1 text-slate-400">
          <MoreVertical className="w-6 h-6" />
          <span className="text-xs">Báo cáo</span>
        </button>
      </div>
    </div>
  );
}
