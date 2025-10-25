import React from 'react';
import { ArrowLeft, Save, User, Mail, Phone, Building2, MapPin, FileText } from 'lucide-react';

export default function CustomerFormMockup() {
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
          <span>Thêm khách hàng</span>
          <Save className="w-6 h-6" />
        </div>
      </div>

      {/* Content */}
      <div className="flex-1 overflow-auto p-4">
        <div className="bg-white rounded-xl p-4 shadow mb-4">
          {/* Avatar Upload */}
          <div className="flex justify-center mb-6">
            <div className="relative">
              <div className="w-24 h-24 bg-gradient-to-br from-[#2196F3] to-[#1976D2] rounded-full flex items-center justify-center text-white text-3xl">
                <User className="w-12 h-12" />
              </div>
              <button className="absolute bottom-0 right-0 w-8 h-8 bg-white rounded-full shadow-lg flex items-center justify-center text-[#2196F3] border-2 border-white">
                <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z" />
                </svg>
              </button>
            </div>
          </div>

          <div className="space-y-4">
            {/* Full Name */}
            <div>
              <label className="text-xs text-slate-500 mb-2 block">Họ và tên *</label>
              <div className="bg-slate-50 rounded-lg px-4 py-3 flex items-center gap-3 border border-slate-200">
                <User className="w-5 h-5 text-slate-400" />
                <input
                  type="text"
                  placeholder="Nhập họ và tên"
                  className="flex-1 bg-transparent outline-none text-slate-900 placeholder:text-slate-400"
                  disabled
                />
              </div>
            </div>

            {/* Email */}
            <div>
              <label className="text-xs text-slate-500 mb-2 block">Email *</label>
              <div className="bg-slate-50 rounded-lg px-4 py-3 flex items-center gap-3 border border-slate-200">
                <Mail className="w-5 h-5 text-slate-400" />
                <input
                  type="email"
                  placeholder="example@company.com"
                  className="flex-1 bg-transparent outline-none text-slate-900 placeholder:text-slate-400"
                  disabled
                />
              </div>
            </div>

            {/* Phone */}
            <div>
              <label className="text-xs text-slate-500 mb-2 block">Số điện thoại *</label>
              <div className="bg-slate-50 rounded-lg px-4 py-3 flex items-center gap-3 border border-slate-200">
                <Phone className="w-5 h-5 text-slate-400" />
                <input
                  type="tel"
                  placeholder="0901234567"
                  className="flex-1 bg-transparent outline-none text-slate-900 placeholder:text-slate-400"
                  disabled
                />
              </div>
            </div>

            {/* Company */}
            <div>
              <label className="text-xs text-slate-500 mb-2 block">Công ty</label>
              <div className="bg-slate-50 rounded-lg px-4 py-3 flex items-center gap-3 border border-slate-200">
                <Building2 className="w-5 h-5 text-slate-400" />
                <input
                  type="text"
                  placeholder="Tên công ty"
                  className="flex-1 bg-transparent outline-none text-slate-900 placeholder:text-slate-400"
                  disabled
                />
              </div>
            </div>

            {/* Address */}
            <div>
              <label className="text-xs text-slate-500 mb-2 block">Địa chỉ</label>
              <div className="bg-slate-50 rounded-lg px-4 py-3 flex items-start gap-3 border border-slate-200">
                <MapPin className="w-5 h-5 text-slate-400 mt-0.5" />
                <textarea
                  placeholder="Nhập địa chỉ đầy đủ"
                  className="flex-1 bg-transparent outline-none text-slate-900 placeholder:text-slate-400 resize-none"
                  rows={2}
                  disabled
                />
              </div>
            </div>

            {/* Notes */}
            <div>
              <label className="text-xs text-slate-500 mb-2 block">Ghi chú</label>
              <div className="bg-slate-50 rounded-lg px-4 py-3 flex items-start gap-3 border border-slate-200">
                <FileText className="w-5 h-5 text-slate-400 mt-0.5" />
                <textarea
                  placeholder="Thông tin bổ sung..."
                  className="flex-1 bg-transparent outline-none text-slate-900 placeholder:text-slate-400 resize-none"
                  rows={3}
                  disabled
                />
              </div>
            </div>

            {/* Status */}
            <div>
              <label className="text-xs text-slate-500 mb-2 block">Trạng thái</label>
              <div className="flex gap-3">
                <button className="flex-1 py-3 rounded-lg bg-[#4CAF50] text-white">
                  Active
                </button>
                <button className="flex-1 py-3 rounded-lg bg-slate-100 text-slate-600">
                  Inactive
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Bottom Actions */}
      <div className="bg-white border-t border-slate-200 p-4 flex gap-3">
        <button className="flex-1 bg-slate-100 text-slate-700 py-3 rounded-lg">
          Hủy
        </button>
        <button className="flex-1 bg-[#2196F3] text-white py-3 rounded-lg flex items-center justify-center gap-2">
          <Save className="w-5 h-5" />
          <span>Lưu</span>
        </button>
      </div>
    </div>
  );
}
