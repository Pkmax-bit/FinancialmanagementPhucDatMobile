import React from 'react';
import { ArrowLeft, Save, Users, FolderKanban, Calendar, Plus, Trash2, DollarSign } from 'lucide-react';

export default function InvoiceFormMockup() {
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
          <span>Tạo hóa đơn mới</span>
          <Save className="w-6 h-6" />
        </div>
      </div>

      {/* Content */}
      <div className="flex-1 overflow-auto p-4">
        {/* Basic Info */}
        <div className="bg-white rounded-xl p-4 shadow mb-4">
          <div className="text-slate-900 mb-3">Thông tin cơ bản</div>
          
          <div className="space-y-3">
            {/* Customer */}
            <div>
              <label className="text-xs text-slate-500 mb-2 block">Khách hàng *</label>
              <div className="bg-slate-50 rounded-lg px-4 py-3 flex items-center justify-between border border-slate-200">
                <div className="flex items-center gap-3">
                  <Users className="w-5 h-5 text-slate-400" />
                  <span className="text-slate-400">Chọn khách hàng</span>
                </div>
              </div>
            </div>

            {/* Project */}
            <div>
              <label className="text-xs text-slate-500 mb-2 block">Dự án *</label>
              <div className="bg-slate-50 rounded-lg px-4 py-3 flex items-center justify-between border border-slate-200">
                <div className="flex items-center gap-3">
                  <FolderKanban className="w-5 h-5 text-slate-400" />
                  <span className="text-slate-400">Chọn dự án</span>
                </div>
              </div>
            </div>

            {/* Dates */}
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="text-xs text-slate-500 mb-2 block">Ngày phát hành</label>
                <div className="bg-slate-50 rounded-lg px-3 py-2 flex items-center gap-2 border border-slate-200">
                  <Calendar className="w-4 h-4 text-slate-400" />
                  <input
                    type="text"
                    placeholder="DD/MM/YY"
                    className="flex-1 bg-transparent outline-none text-slate-900 placeholder:text-slate-400 text-sm"
                    disabled
                  />
                </div>
              </div>
              <div>
                <label className="text-xs text-slate-500 mb-2 block">Hạn thanh toán *</label>
                <div className="bg-slate-50 rounded-lg px-3 py-2 flex items-center gap-2 border border-slate-200">
                  <Calendar className="w-4 h-4 text-slate-400" />
                  <input
                    type="text"
                    placeholder="DD/MM/YY"
                    className="flex-1 bg-transparent outline-none text-slate-900 placeholder:text-slate-400 text-sm"
                    disabled
                  />
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Items */}
        <div className="bg-white rounded-xl p-4 shadow mb-4">
          <div className="flex items-center justify-between mb-3">
            <span className="text-slate-900">Chi tiết dịch vụ</span>
            <button className="text-[#2196F3] text-sm flex items-center gap-1">
              <Plus className="w-4 h-4" />
              Thêm
            </button>
          </div>

          <div className="space-y-3">
            {/* Item 1 */}
            <div className="border border-slate-200 rounded-lg p-3">
              <div className="flex items-start justify-between mb-2">
                <input
                  type="text"
                  placeholder="Tên dịch vụ"
                  className="flex-1 outline-none text-slate-900 placeholder:text-slate-400"
                  disabled
                />
                <button className="text-slate-400">
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
              <textarea
                placeholder="Mô tả..."
                className="w-full text-sm text-slate-600 outline-none placeholder:text-slate-400 resize-none"
                rows={2}
                disabled
              />
              <div className="grid grid-cols-3 gap-2 mt-2">
                <input
                  type="number"
                  placeholder="SL"
                  className="bg-slate-50 rounded px-2 py-2 text-sm outline-none text-slate-900 placeholder:text-slate-400"
                  disabled
                />
                <input
                  type="number"
                  placeholder="Đơn giá"
                  className="bg-slate-50 rounded px-2 py-2 text-sm outline-none text-slate-900 placeholder:text-slate-400"
                  disabled
                />
                <div className="bg-slate-100 rounded px-2 py-2 text-sm text-slate-600 text-right">
                  $0
                </div>
              </div>
            </div>

            {/* Placeholder */}
            <div className="border border-dashed border-slate-300 rounded-lg p-4 text-center">
              <Plus className="w-6 h-6 text-slate-400 mx-auto mb-1" />
              <div className="text-xs text-slate-400">Thêm dịch vụ</div>
            </div>
          </div>

          {/* Totals */}
          <div className="mt-4 pt-3 border-t border-slate-200 space-y-2">
            <div className="flex justify-between text-sm">
              <span className="text-slate-600">Tạm tính</span>
              <span className="text-slate-900">$0.00</span>
            </div>
            <div className="flex justify-between text-sm">
              <span className="text-slate-600">VAT (10%)</span>
              <span className="text-slate-900">$0.00</span>
            </div>
            <div className="flex justify-between pt-2 border-t border-slate-200">
              <span className="text-slate-900">Tổng cộng</span>
              <span className="text-[#2196F3] text-xl">$0.00</span>
            </div>
          </div>
        </div>

        {/* Payment Terms */}
        <div className="bg-white rounded-xl p-4 shadow mb-4">
          <div className="text-slate-900 mb-3">Điều khoản thanh toán</div>
          
          <div className="space-y-3">
            <div>
              <label className="text-xs text-slate-500 mb-2 block">Phương thức thanh toán</label>
              <div className="bg-slate-50 rounded-lg px-4 py-3 border border-slate-200">
                <select className="w-full bg-transparent outline-none text-slate-900" disabled>
                  <option>Chuyển khoản ngân hàng</option>
                </select>
              </div>
            </div>

            <div>
              <label className="text-xs text-slate-500 mb-2 block">Số tài khoản</label>
              <div className="bg-slate-50 rounded-lg px-4 py-3 flex items-center gap-3 border border-slate-200">
                <DollarSign className="w-5 h-5 text-slate-400" />
                <input
                  type="text"
                  placeholder="0123456789"
                  className="flex-1 bg-transparent outline-none text-slate-900 placeholder:text-slate-400"
                  disabled
                />
              </div>
            </div>
          </div>
        </div>

        {/* Notes */}
        <div className="bg-white rounded-xl p-4 shadow">
          <div className="text-slate-900 mb-3">Ghi chú</div>
          <textarea
            placeholder="Ghi chú thêm..."
            className="w-full bg-slate-50 rounded-lg px-4 py-3 outline-none text-sm text-slate-700 placeholder:text-slate-400 resize-none"
            rows={3}
            disabled
          />
        </div>
      </div>

      {/* Bottom Actions */}
      <div className="bg-white border-t border-slate-200 p-4 flex gap-3">
        <button className="flex-1 bg-slate-100 text-slate-700 py-3 rounded-lg">
          Lưu nháp
        </button>
        <button className="flex-1 bg-[#2196F3] text-white py-3 rounded-lg flex items-center justify-center gap-2">
          <Save className="w-5 h-5" />
          <span>Tạo hóa đơn</span>
        </button>
      </div>
    </div>
  );
}
