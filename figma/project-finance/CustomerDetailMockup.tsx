import React from 'react';
import { ArrowLeft, Edit, Mail, Phone, Building2, MapPin, FileText, FolderKanban, Receipt } from 'lucide-react';

export default function CustomerDetailMockup() {
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
        <div className="flex items-center justify-between">
          <ArrowLeft className="w-6 h-6" />
          <span>Chi tiết khách hàng</span>
          <Edit className="w-6 h-6" />
        </div>
      </div>

      {/* Content */}
      <div className="flex-1 overflow-auto">
        {/* Header Card */}
        <div className="bg-white p-6 border-b border-slate-200">
          <div className="flex items-start gap-4 mb-4">
            <div className="w-16 h-16 bg-gradient-to-br from-[#2196F3] to-[#1976D2] rounded-full flex items-center justify-center text-white text-2xl flex-shrink-0">
              N
            </div>
            <div className="flex-1">
              <div className="text-slate-900 mb-1">Nguyễn Văn A</div>
              <div className="bg-[#4CAF50]/10 text-[#4CAF50] px-2 py-1 rounded text-xs inline-block">
                Active
              </div>
            </div>
          </div>

          {/* Contact Info */}
          <div className="space-y-3">
            <div className="flex items-center gap-3">
              <Building2 className="w-5 h-5 text-[#2196F3]" />
              <div>
                <div className="text-xs text-slate-500">Công ty</div>
                <div className="text-slate-900">ABC Corporation</div>
              </div>
            </div>

            <div className="flex items-center gap-3">
              <Mail className="w-5 h-5 text-[#2196F3]" />
              <div>
                <div className="text-xs text-slate-500">Email</div>
                <div className="text-slate-900">nguyenvana@abc.com</div>
              </div>
            </div>

            <div className="flex items-center gap-3">
              <Phone className="w-5 h-5 text-[#2196F3]" />
              <div>
                <div className="text-xs text-slate-500">Điện thoại</div>
                <div className="text-slate-900">0901234567</div>
              </div>
            </div>

            <div className="flex items-center gap-3">
              <MapPin className="w-5 h-5 text-[#2196F3]" />
              <div>
                <div className="text-xs text-slate-500">Địa chỉ</div>
                <div className="text-slate-900">123 Nguyễn Huệ, Q.1, TP.HCM</div>
              </div>
            </div>
          </div>
        </div>

        {/* Tabs */}
        <div className="bg-white border-b border-slate-200">
          <div className="flex">
            <button className="flex-1 py-3 text-[#2196F3] border-b-2 border-[#2196F3] text-sm">
              Thông tin
            </button>
            <button className="flex-1 py-3 text-slate-400 text-sm">
              Dự án
            </button>
            <button className="flex-1 py-3 text-slate-400 text-sm">
              Hóa đơn
            </button>
            <button className="flex-1 py-3 text-slate-400 text-sm">
              Báo giá
            </button>
          </div>
        </div>

        {/* Statistics */}
        <div className="p-4">
          <div className="grid grid-cols-3 gap-3 mb-4">
            <div className="bg-white rounded-xl p-3 shadow text-center">
              <div className="w-10 h-10 bg-[#2196F3]/10 rounded-lg flex items-center justify-center mx-auto mb-2">
                <FolderKanban className="w-5 h-5 text-[#2196F3]" />
              </div>
              <div className="text-xl text-slate-900 mb-1">5</div>
              <div className="text-xs text-slate-500">Dự án</div>
            </div>

            <div className="bg-white rounded-xl p-3 shadow text-center">
              <div className="w-10 h-10 bg-[#4CAF50]/10 rounded-lg flex items-center justify-center mx-auto mb-2">
                <Receipt className="w-5 h-5 text-[#4CAF50]" />
              </div>
              <div className="text-xl text-slate-900 mb-1">12</div>
              <div className="text-xs text-slate-500">Hóa đơn</div>
            </div>

            <div className="bg-white rounded-xl p-3 shadow text-center">
              <div className="w-10 h-10 bg-[#FF9800]/10 rounded-lg flex items-center justify-center mx-auto mb-2">
                <FileText className="w-5 h-5 text-[#FF9800]" />
              </div>
              <div className="text-xl text-slate-900 mb-1">8</div>
              <div className="text-xs text-slate-500">Báo giá</div>
            </div>
          </div>

          {/* Revenue Summary */}
          <div className="bg-gradient-to-r from-[#4CAF50] to-[#45a049] rounded-xl p-4 text-white shadow mb-4">
            <div className="text-sm opacity-90 mb-1">Tổng doanh thu</div>
            <div className="text-2xl mb-2">$125,450</div>
            <div className="flex justify-between text-xs opacity-80">
              <span>Đã thanh toán: $98,320</span>
              <span>Chờ: $27,130</span>
            </div>
          </div>

          {/* Notes */}
          <div className="bg-white rounded-xl p-4 shadow">
            <div className="text-slate-900 mb-2">Ghi chú</div>
            <div className="text-sm text-slate-600">
              Khách hàng VIP, ưu tiên hỗ trợ. Thời gian thanh toán trung bình: 15 ngày. 
              Liên hệ chính: Mr. Nguyễn (CEO)
            </div>
          </div>
        </div>
      </div>

      {/* Bottom Actions */}
      <div className="bg-white border-t border-slate-200 p-4 flex gap-3">
        <button className="flex-1 bg-[#2196F3] text-white py-3 rounded-lg">
          Tạo dự án mới
        </button>
        <button className="flex-1 bg-slate-100 text-slate-700 py-3 rounded-lg">
          Tạo báo giá
        </button>
      </div>
    </div>
  );
}
